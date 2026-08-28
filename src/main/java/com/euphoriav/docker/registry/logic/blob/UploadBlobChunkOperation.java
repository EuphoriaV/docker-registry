package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.aop.annotation.ValidName;
import com.euphoriav.docker.registry.dao.BlobUploadDao;
import com.euphoriav.docker.registry.dto.ErrorResponse;
import com.euphoriav.docker.registry.exception.InvalidRequestException;
import com.euphoriav.docker.registry.exception.NotFoundException;
import com.euphoriav.docker.registry.logic.helper.UploadChunkHelper;
import com.euphoriav.docker.registry.logic.lock.LockService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UploadBlobChunkOperation {

    private final BlobUploadDao blobUploadDao;
    private final UploadChunkHelper uploadChunkHelper;
    private final LockService lockService;

    @ValidName
    public long activate(String name, UUID id, Resource body, String range) {
        return lockService.tryInLock(id.toString(), () -> uploadChunk(name, id, body, range));
    }

    private long uploadChunk(String name, UUID id, Resource body, String range) {
        long contentLength;
        try {
            contentLength = body.contentLength();
        } catch (IOException e) {
            throw new InvalidRequestException("could not get content length", ErrorResponse.ErrorCode.BLOB_UPLOAD_INVALID);
        }

        var blobUploadOptional = blobUploadDao.find(id, name);
        if (blobUploadOptional.isEmpty()) {
            throw new NotFoundException("blob upload unknown to registry", ErrorResponse.ErrorCode.BLOB_UPLOAD_UNKNOWN);
        }
        var blobUpload = blobUploadOptional.get();

        long startRange = blobUpload.getBytesReceived(), endRange = startRange + contentLength - 1;
        uploadChunkHelper.compareRangeAndUploadChunk(body, range, startRange, endRange, id);

        blobUploadDao.updateBytesReceived(blobUpload.getId(), contentLength);
        return endRange;
    }
}
