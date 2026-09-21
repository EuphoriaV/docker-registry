package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.dao.BlobUploadDao;
import com.euphoriav.docker.registry.enums.ErrorCode;
import com.euphoriav.docker.registry.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetBlobUploadStatusOperation {

    private final BlobUploadDao blobUploadDao;

    public long activate(String name, UUID id) {
        return blobUploadDao.find(id, name)
                .orElseThrow(() -> new NotFoundException("blob upload unknown to registry", ErrorCode.BLOB_UPLOAD_UNKNOWN))
                .getBytesReceived();
    }
}
