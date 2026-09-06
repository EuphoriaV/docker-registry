package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.dao.BlobDao;
import com.euphoriav.docker.registry.enums.ErrorCode;
import com.euphoriav.docker.registry.exception.NotFoundException;
import com.euphoriav.docker.registry.logic.blob.upload.BlobUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteBlobOperation {

    private final BlobUploader blobUploader;
    private final BlobDao blobDao;

    @Transactional
    public void activate(String name, String digest) {
        var blobOptional = blobDao.findForUpdate(digest, name);
        if (blobOptional.isEmpty()) {
            throw new NotFoundException("blob unknown to registry", ErrorCode.BLOB_UNKNOWN);
        }

        blobDao.delete(name, digest);
        try {
            blobUploader.delete(blobOptional.get().getFilename());
        } catch (IOException e) {
            log.error("failed to delete file", e);
        }
    }
}
