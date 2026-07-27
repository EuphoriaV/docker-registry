package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.dao.BlobUploadDao;
import com.euphoriav.docker.registry.exception.InternalServerException;
import com.euphoriav.docker.registry.exception.InvalidRangeException;
import com.euphoriav.docker.registry.exception.InvalidRequestException;
import com.euphoriav.docker.registry.exception.NotFoundException;
import com.euphoriav.docker.registry.logic.blob.upload.BlobUploader;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.euphoriav.docker.registry.dto.ErrorResponse.ErrorCode.BLOB_UPLOAD_UNKNOWN;
import static com.euphoriav.docker.registry.dto.ErrorResponse.ErrorCode.SIZE_INVALID;

@Component
@RequiredArgsConstructor
public class UploadBlobChunkOperation {

    private final BlobUploadDao blobUploadDao;
    private final BlobUploader blobUploader;

    @Transactional(rollbackFor = Exception.class)
    public long activate(String name, UUID id, Resource body, String range, long contentLength) {
        if (contentLength < 0) {
            throw new InvalidRequestException("content-length header is required", SIZE_INVALID);
        }

        var blobUpload = blobUploadDao.findUploadInProgress(id, name);
        if (blobUpload.isEmpty()) {
            throw new NotFoundException("blob upload unknown to registry", BLOB_UPLOAD_UNKNOWN);
        }

        long startRange = blobUpload.get().getBytesReceived(), endRange = startRange + contentLength - 1;
        var expectedRange = "%d-%d".formatted(startRange, endRange);
        if (StringUtils.isNotEmpty(range) && !expectedRange.equals(range)) {
            throw new InvalidRangeException("provided length did not match content length");
        }

        blobUploadDao.updateBytesReceived(id, name, contentLength);

        try {
            blobUploader.uploadChunk(id, body.getInputStream());
        } catch (Exception e) {
            throw new InternalServerException("Could not upload chunk", e);
        }

        return endRange;
    }
}
