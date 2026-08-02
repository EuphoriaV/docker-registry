package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.dao.BlobDao;
import com.euphoriav.docker.registry.dto.ErrorResponse;
import com.euphoriav.docker.registry.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckBlobExistsOperation {

    private final BlobDao blobDao;

    public long activate(String name, String digest) {
        var blobOptional = blobDao.find(digest, name);
        if (blobOptional.isEmpty()) {
            throw new NotFoundException("blob unknown to registry", ErrorResponse.ErrorCode.BLOB_UNKNOWN);
        }
        return blobOptional.get().getSize();
    }
}
