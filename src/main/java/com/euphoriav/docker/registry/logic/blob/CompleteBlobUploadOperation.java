package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.aop.annotation.ValidName;
import com.euphoriav.docker.registry.dao.BlobDao;
import com.euphoriav.docker.registry.dao.BlobUploadDao;
import com.euphoriav.docker.registry.dto.ErrorResponse;
import com.euphoriav.docker.registry.exception.InternalServerException;
import com.euphoriav.docker.registry.exception.InvalidRequestException;
import com.euphoriav.docker.registry.exception.NotFoundException;
import com.euphoriav.docker.registry.logic.helper.DigestHelper;
import com.euphoriav.docker.registry.logic.helper.UploadChunkHelper;
import com.euphoriav.docker.registry.logic.lock.LockService;
import com.euphoriav.docker.registry.logic.blob.upload.BlobUploader;
import com.euphoriav.docker.registry.model.BlobUpload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompleteBlobUploadOperation {

    private final BlobUploadDao blobUploadDao;
    private final BlobDao blobDao;
    private final BlobUploader blobUploader;
    private final LockService lockService;
    private final UploadChunkHelper uploadChunkHelper;
    private final DigestHelper digestHelper;

    @Lazy
    @Autowired
    private CompleteBlobUploadOperation self;

    @ValidName
    public void activate(String name, UUID id, String digest, String range, Resource body) {
        lockService.tryInLock(id.toString(), () -> completeUpload(name, id, digest, range, body));
    }

    private void completeUpload(String name, UUID id, String digest, String range, Resource body) {
        long contentLength;
        try {
            contentLength = body == null ? 0 : body.contentLength();
        } catch (IOException e) {
            throw new InvalidRequestException("could not get content length", ErrorResponse.ErrorCode.BLOB_UPLOAD_INVALID);
        }

        var blobUploadOptional = blobUploadDao.find(id, name);
        if (blobUploadOptional.isEmpty()) {
            throw new NotFoundException("blob upload unknown to registry", ErrorResponse.ErrorCode.BLOB_UPLOAD_UNKNOWN);
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
            actualDigest = digestHelper.calculateDigest(blobUploader.getInputStream(blobUpload.getId().toString()));
        } catch (Exception e) {
            throw new InternalServerException("could not calculate actual digest", e);
        }

        if (!digest.equals(actualDigest)) {
            throw new InvalidRequestException("provided digest did not match uploaded content", ErrorResponse.ErrorCode.DIGEST_INVALID);
        }

        self.createBlob(blobUpload, digest, size, id);
    }

    @Transactional
    public void createBlob(BlobUpload blobUpload, String digest, long size, UUID id) {
        blobUploadDao.delete(blobUpload.getId());
        blobDao.create(blobUpload.getRepository(), digest, size, id);
    }
}
