package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.aop.annotation.ValidName;
import com.euphoriav.docker.registry.dao.BlobUploadDao;
import com.euphoriav.docker.registry.exception.InvalidRequestException;
import com.euphoriav.docker.registry.exception.NotFoundException;
import com.euphoriav.docker.registry.logic.blob.helper.UploadChunkHelper;
import com.euphoriav.docker.registry.logic.blob.lock.LockService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.euphoriav.docker.registry.dto.ErrorResponse.ErrorCode.BLOB_UPLOAD_UNKNOWN;
import static com.euphoriav.docker.registry.dto.ErrorResponse.ErrorCode.SIZE_INVALID;

@Component
@RequiredArgsConstructor
public class UploadBlobChunkOperation {

    private final BlobUploadDao blobUploadDao;
    private final UploadChunkHelper uploadChunkHelper;
    private final LockService lockService;

    @ValidName
    public long activate(String name, UUID id, Resource body, String range, long contentLength) {
        if (contentLength < 0) {
            throw new InvalidRequestException("content-length header is required", SIZE_INVALID);
        }

        return lockService.tryInLock(id, () -> uploadChunk(name, id, body, range, contentLength));
    }

    private long uploadChunk(String name, UUID id, Resource body, String range, long contentLength) {
        var blobUploadOptional = blobUploadDao.find(id, name);
        if (blobUploadOptional.isEmpty()) {
            throw new NotFoundException("blob upload unknown to registry", BLOB_UPLOAD_UNKNOWN);
        }
        var blobUpload = blobUploadOptional.get();

        long startRange = blobUpload.getBytesReceived(), endRange = startRange + contentLength - 1;
        uploadChunkHelper.compareRangeAndUploadChunk(body, range, startRange, endRange, id);

        blobUploadDao.updateBytesReceived(blobUpload.getId(), contentLength);
        return endRange;
    }
}
