package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.aop.annotation.ValidName;
import com.euphoriav.docker.registry.dao.BlobUploadDao;
import com.euphoriav.docker.registry.dto.ErrorResponse;
import com.euphoriav.docker.registry.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetBlobUploadStatusOperation {

    private final BlobUploadDao blobUploadDao;

    @ValidName
    public long activate(String name, UUID id) {
        var blobUploadOptional = blobUploadDao.find(id, name);
        if (blobUploadOptional.isEmpty()) {
            throw new NotFoundException("blob upload unknown to registry", ErrorResponse.ErrorCode.BLOB_UPLOAD_UNKNOWN);
        }
        return blobUploadOptional.get().getBytesReceived();
    }
}
