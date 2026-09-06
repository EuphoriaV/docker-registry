package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.dao.BlobUploadDao;
import com.euphoriav.docker.registry.exception.InternalServerException;
import com.euphoriav.docker.registry.logic.blob.upload.BlobUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InitiateBlobUploadOperation {

    private final BlobUploader blobUploader;
    private final BlobUploadDao blobUploadDao;

    public UUID activate(String name) {
        var id = UUID.randomUUID();

        try {
            blobUploader.initUpload(id);
        } catch (Exception e) {
            throw new InternalServerException("could not create initial blob file", e);
        }

        blobUploadDao.insert(id, name);
        return id;
    }
}
