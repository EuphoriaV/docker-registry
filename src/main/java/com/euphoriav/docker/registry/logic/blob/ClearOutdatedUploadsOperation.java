package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.dao.BlobUploadDao;
import com.euphoriav.docker.registry.logic.blob.upload.BlobUploader;
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

    @Value("${app.clear-uploads.ttl-minutes}")
    private int ttlMinutes;

    public void activate() {
        var ids = blobUploadDao.clearOutdatedUploads(ttlMinutes);
        ids.forEach(id -> {
            try {
                blobUploader.delete(id);
            } catch (Exception e) {
                log.error("failed to delete file", e);
            }
        });
    }
}
