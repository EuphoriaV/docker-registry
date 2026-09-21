package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.dao.BlobUploadDao;
import com.euphoriav.docker.registry.logic.blob.upload.BlobUploader;
import com.euphoriav.docker.registry.logic.lock.LockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClearOutdatedUploadsOperation {

    private final BlobUploadDao blobUploadDao;
    private final BlobUploader blobUploader;
    private final LockService lockService;

    @Value("${app.clear-uploads.ttl-minutes}")
    private int ttlMinutes;

    public void activate() {
        var ids = blobUploadDao.getOutdatedUploads(ttlMinutes);
        ids.forEach(id -> lockService.tryInLock(id.toString(), () -> {
            blobUploadDao.delete(id);
            try {
                blobUploader.delete(id.toString());
            } catch (Exception e) {
                log.error("failed to delete file", e);
            }
        }));
    }
}
