package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.dao.BlobUploadDao;
import com.euphoriav.docker.registry.exception.InternalServerException;
import com.euphoriav.docker.registry.exception.InvalidRangeException;
import com.euphoriav.docker.registry.exception.InvalidRequestException;
import com.euphoriav.docker.registry.exception.NotFoundException;
import com.euphoriav.docker.registry.logic.blob.lock.LockService;
import com.euphoriav.docker.registry.logic.blob.upload.BlobUploader;
import com.euphoriav.docker.registry.model.BlobUpload;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.euphoriav.docker.registry.dto.ErrorResponse.ErrorCode.BLOB_UPLOAD_UNKNOWN;
import static com.euphoriav.docker.registry.dto.ErrorResponse.ErrorCode.SIZE_INVALID;

@Component
@RequiredArgsConstructor
public class UploadBlobChunkOperation {

    private final BlobUploadDao blobUploadDao;
    private final BlobUploader blobUploader;
    private final LockService lockService;

    public long activate(String name, UUID id, Resource body, String range, long contentLength) {
        if (contentLength < 0) {
            throw new InvalidRequestException("content-length header is required", SIZE_INVALID);
        }
        var blobUpload = blobUploadDao.find(id, name);
        if (blobUpload.isEmpty()) {
            throw new NotFoundException("blob upload unknown to registry", BLOB_UPLOAD_UNKNOWN);
        }

        return lockService.tryInLock(id, () -> uploadChunk(blobUpload.get(), body, range, contentLength));
    }

    public long uploadChunk(BlobUpload blobUpload, Resource body, String range, long contentLength) {
        long startRange = blobUpload.getBytesReceived(), endRange = startRange + contentLength - 1;

        var expectedRange = "%d-%d".formatted(startRange, endRange);
        if (StringUtils.isNotEmpty(range) && !expectedRange.equals(range)) {
            throw new InvalidRangeException("range is out of order");
        }

        try {
            blobUploader.uploadChunk(blobUpload.getId(), body.getInputStream(), startRange);
        } catch (Exception e) {
            throw new InternalServerException("could not upload chunk", e);
        }

        blobUploadDao.updateBytesReceived(blobUpload.getId(), contentLength);
        return endRange;
    }
}
