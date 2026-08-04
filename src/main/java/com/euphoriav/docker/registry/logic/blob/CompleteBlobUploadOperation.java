package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.dao.BlobDao;
import com.euphoriav.docker.registry.dao.BlobUploadDao;
import com.euphoriav.docker.registry.exception.InternalServerException;
import com.euphoriav.docker.registry.exception.InvalidRequestException;
import com.euphoriav.docker.registry.exception.NotFoundException;
import com.euphoriav.docker.registry.logic.blob.helper.UploadChunkHelper;
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
    private final UploadChunkHelper uploadChunkHelper;

    @Lazy
    @Autowired
    private CompleteBlobUploadOperation self;

    public void activate(String name, UUID id, String digest, String range, Resource body, long contentLength) {
        lockService.tryInLock(id, () -> completeUpload(name, id, digest, range, body, contentLength));
    }

    private void completeUpload(String name, UUID id, String digest, String range, Resource body, long contentLength) {
        var blobUploadOptional = blobUploadDao.find(id, name);
        if (blobUploadOptional.isEmpty()) {
            throw new NotFoundException("blob upload unknown to registry", BLOB_UPLOAD_UNKNOWN);
        }
        var blobUpload = blobUploadOptional.get();

        long size = blobUpload.getBytesReceived();
        if (contentLength > 0) {
            long startRange = blobUpload.getBytesReceived(), endRange = startRange + contentLength - 1;
            size = endRange + 1;

            uploadChunkHelper.compareRangeAndUploadChunk(body, range, startRange, endRange, id);
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

        self.createBlob(blobUpload, digest, size, id);
    }

    @Transactional
    public void createBlob(BlobUpload blobUpload, String digest, long size, UUID id) {
        blobUploadDao.delete(blobUpload.getId());
        blobDao.create(blobUpload.getRepository(), digest, size, id);
    }
}
