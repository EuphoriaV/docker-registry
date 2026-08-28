package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.aop.annotation.ValidName;
import com.euphoriav.docker.registry.dao.BlobUploadDao;
import com.euphoriav.docker.registry.dto.ErrorResponse;
import com.euphoriav.docker.registry.exception.NotFoundException;
import com.euphoriav.docker.registry.logic.blob.lock.LockService;
import com.euphoriav.docker.registry.logic.blob.upload.BlobUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CancelBlobUploadOperation {

    private final BlobUploader blobUploader;
    private final BlobUploadDao blobUploadDao;
    private final LockService lockService;

    @ValidName
    public void activate(String name, UUID id) {
        lockService.tryInLock(id.toString(), () -> cancelUpload(name, id));
    }

    private void cancelUpload(String name, UUID id) {
        var blobUploadOptional = blobUploadDao.find(id, name);
        if (blobUploadOptional.isEmpty()) {
            throw new NotFoundException("blob upload unknown to registry", ErrorResponse.ErrorCode.BLOB_UPLOAD_UNKNOWN);
        }
        blobUploadDao.delete(id);
        try {
            blobUploader.delete(id);
        } catch (Exception e) {
            log.error("failed to delete file", e);
        }
    }
}
