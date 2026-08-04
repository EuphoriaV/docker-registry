package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.dao.BlobDao;
import com.euphoriav.docker.registry.dto.ErrorResponse;
import com.euphoriav.docker.registry.exception.InternalServerException;
import com.euphoriav.docker.registry.exception.NotFoundException;
import com.euphoriav.docker.registry.logic.blob.upload.BlobUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class GetBlobOperation {

    private final BlobUploader blobUploader;
    private final BlobDao blobDao;

    public Response activate(String name, String digest) {
        var blobOptional = blobDao.find(digest, name);
        if (blobOptional.isEmpty()) {
            throw new NotFoundException("blob unknown to registry", ErrorResponse.ErrorCode.BLOB_UNKNOWN);
        }
        var blob = blobOptional.get();

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
