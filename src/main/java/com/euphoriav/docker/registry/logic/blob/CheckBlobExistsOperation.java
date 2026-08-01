package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.dao.BlobUploadDao;
import com.euphoriav.docker.registry.logic.blob.upload.BlobUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckBlobExistsOperation {

    private final BlobUploader blobUploader;
    private final BlobUploadDao blobUploadDao;

    public long activate(String name, String digest) {
        return 0;
    }
}
