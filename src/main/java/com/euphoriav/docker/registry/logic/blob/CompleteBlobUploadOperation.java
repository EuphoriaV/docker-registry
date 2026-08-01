package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.dao.BlobDao;
import com.euphoriav.docker.registry.dao.BlobUploadDao;
import com.euphoriav.docker.registry.exception.InternalServerException;
import com.euphoriav.docker.registry.exception.InvalidRequestException;
import com.euphoriav.docker.registry.exception.NotFoundException;
import com.euphoriav.docker.registry.logic.blob.lock.LockService;
import com.euphoriav.docker.registry.logic.blob.upload.BlobUploader;
import com.euphoriav.docker.registry.model.BlobUpload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.euphoriav.docker.registry.dto.ErrorResponse.ErrorCode.BLOB_UPLOAD_UNKNOWN;
import static com.euphoriav.docker.registry.dto.ErrorResponse.ErrorCode.DIGEST_INVALID;

@Component
@RequiredArgsConstructor
public class CompleteBlobUploadOperation {

    private final BlobUploadDao blobUploadDao;
    private final BlobDao blobDao;
    private final BlobUploader blobUploader;
    private final LockService lockService;
    private final UploadBlobChunkOperation uploadBlobChunkOperation;

    @Lazy
    @Autowired
    private CompleteBlobUploadOperation self;

    public void activate(String name, UUID id, String digest, String range, Resource body, long contentLength) {
        var blobUpload = blobUploadDao.find(id, name);
        if (blobUpload.isEmpty()) {
            throw new NotFoundException("blob upload unknown to registry", BLOB_UPLOAD_UNKNOWN);
        }

        lockService.tryInLock(id, () -> {
            completeUpload(blobUpload.get(), digest, range, body, contentLength);
            return null;
        });
    }

    private void completeUpload(BlobUpload blobUpload, String digest, String range, Resource body, long contentLength) {
        try {
            long size = blobUpload.getBytesReceived();
            if (contentLength > 0) {
                size = uploadBlobChunkOperation.uploadChunk(blobUpload, body, range, contentLength) + 1;
            }

            String actualDigest;
            try {
                actualDigest = blobUploader.computeDigest(blobUpload.getId());
            } catch (Exception e) {
                throw new InternalServerException("could not calculate actual digest", e);
            }

            if (!digest.equals(actualDigest)) {
                throw new InvalidRequestException("provided digest did not match uploaded content", DIGEST_INVALID);
            }

            self.createBlob(blobUpload, digest, size);
        } catch (Exception e) {
            blobUploadDao.delete(blobUpload.getId());
            blobUploader.delete(blobUpload.getId());
            throw e;
        }
    }

    @Transactional
    public void createBlob(BlobUpload blobUpload, String digest, long size) {
        blobUploadDao.delete(blobUpload.getId());
        blobDao.create(blobUpload.getRepository(), digest, size);
    }
}
