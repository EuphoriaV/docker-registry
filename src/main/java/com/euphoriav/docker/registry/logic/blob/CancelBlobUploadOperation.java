package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.dao.BlobUploadDao;
import com.euphoriav.docker.registry.exception.NotFoundException;
import com.euphoriav.docker.registry.logic.blob.lock.LockService;
import com.euphoriav.docker.registry.logic.blob.upload.BlobUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.euphoriav.docker.registry.dto.ErrorResponse.ErrorCode.BLOB_UPLOAD_UNKNOWN;

@Component
@RequiredArgsConstructor
public class CancelBlobUploadOperation {

    private final BlobUploader blobUploader;
    private final BlobUploadDao blobUploadDao;
    private final LockService lockService;

    public void activate(String name, UUID id) {
        lockService.tryInLock(id, () -> cancelUpload(name, id));
    }

    private void cancelUpload(String name, UUID id) {
        var blobUploadOptional = blobUploadDao.find(id, name);
        if (blobUploadOptional.isEmpty()) {
            throw new NotFoundException("blob upload unknown to registry", BLOB_UPLOAD_UNKNOWN);
        }
        blobUploader.delete(id);
        blobUploadDao.delete(id);
    }
}
