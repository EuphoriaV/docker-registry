package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.dao.BlobDao;
import com.euphoriav.docker.registry.enums.ErrorCode;
import com.euphoriav.docker.registry.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckBlobExistsOperation {

    private final BlobDao blobDao;

    public long activate(String name, String digest) {
        return blobDao.find(digest, name)
                .orElseThrow(() -> new NotFoundException("blob unknown to registry", ErrorCode.BLOB_UNKNOWN))
                .getSize();
    }
}
