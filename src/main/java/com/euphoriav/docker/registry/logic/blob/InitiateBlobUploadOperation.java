package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.dao.BlobUploadDao;
import com.euphoriav.docker.registry.exception.InternalServerException;
import com.euphoriav.docker.registry.logic.blob.upload.BlobUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InitiateBlobUploadOperation {

    private final BlobUploader blobUploader;
    private final BlobUploadDao blobUploadDao;

    @Transactional(rollbackFor = Exception.class)
    public UUID activate(String name) {
        var id = UUID.randomUUID();
        blobUploadDao.insert(id, name);

        try {
            blobUploader.initUpload(id);
        } catch (Exception e) {
            throw new InternalServerException("Could not initiate blob upload", e);
        }
        return id;
    }
}
