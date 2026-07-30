package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.dao.BlobUploadDao;
import com.euphoriav.docker.registry.exception.InternalServerException;
import com.euphoriav.docker.registry.exception.InvalidRangeException;
import com.euphoriav.docker.registry.exception.InvalidRequestException;
import com.euphoriav.docker.registry.exception.NotFoundException;
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

    public long activate(String name, UUID id, Resource body, String range, long contentLength) {
        if (contentLength < 0) {
            throw new InvalidRequestException("content-length header is required", SIZE_INVALID);
        }

        var locked = blobUploadDao.lockUpload(id, name);
        if (!locked) {
            throw new NotFoundException("blob upload unknown to registry or could not be locked", BLOB_UPLOAD_UNKNOWN);
        }

        try {
            return uploadChunk(id, body, range, contentLength);
        } finally {
            blobUploadDao.updateStatus(id, BlobUpload.UploadStatus.IDLE);
        }
    }

    public long uploadChunk(UUID id, Resource body, String range, long contentLength) {
        long startRange, endRange;
        try {
            startRange = blobUploader.getSize(id);
            endRange = startRange + contentLength - 1;
        } catch (Exception e) {
            throw new InternalServerException("could not get blob size", e);
        }

        var expectedRange = "%d-%d".formatted(startRange, endRange);
        if (StringUtils.isNotEmpty(range) && !expectedRange.equals(range)) {
            throw new InvalidRangeException("range is out of order");
        }

        try {
            blobUploader.uploadChunk(id, body.getInputStream(), startRange);
        } catch (Exception e) {
            throw new InternalServerException("could not upload chunk", e);
        }

        return endRange;
    }
}
