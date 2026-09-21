package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.dao.BlobDao;
import com.euphoriav.docker.registry.enums.ErrorCode;
import com.euphoriav.docker.registry.exception.InternalServerException;
import com.euphoriav.docker.registry.exception.NotFoundException;
import com.euphoriav.docker.registry.logic.blob.upload.BlobUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class GetBlobOperation {

    private final BlobUploader blobUploader;
    private final BlobDao blobDao;

    @Transactional
    public Response activate(String name, String digest) {
        var blob = blobDao.find(digest, name)
                .orElseThrow(() -> new NotFoundException("blob unknown to registry", ErrorCode.BLOB_UNKNOWN));

        Resource resource;
        try {
            resource = new InputStreamResource(blobUploader.getInputStream(blob.getFilename()));
        } catch (IOException e) {
            throw new InternalServerException("could not read blob", e);
        }
        return new Response(blob.getSize(), resource);
    }

    public record Response(long size, Resource resource) {
    }
}
