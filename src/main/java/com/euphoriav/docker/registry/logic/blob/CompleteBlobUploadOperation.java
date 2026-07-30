package com.euphoriav.docker.registry.logic.blob;

import com.euphoriav.docker.registry.dao.BlobUploadDao;
import com.euphoriav.docker.registry.exception.InternalServerException;
import com.euphoriav.docker.registry.exception.InvalidRequestException;
import com.euphoriav.docker.registry.exception.NotFoundException;
import com.euphoriav.docker.registry.logic.blob.upload.BlobUploader;
import com.euphoriav.docker.registry.model.BlobUpload;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.euphoriav.docker.registry.dto.ErrorResponse.ErrorCode.BLOB_UPLOAD_UNKNOWN;
import static com.euphoriav.docker.registry.dto.ErrorResponse.ErrorCode.DIGEST_INVALID;

@Component
@RequiredArgsConstructor
public class CompleteBlobUploadOperation {

    private final BlobUploadDao blobUploadDao;
    private final BlobUploader blobUploader;
    private final UploadBlobChunkOperation uploadBlobChunkOperation;

    public void activate(String name, UUID id, String digest, String range, Resource body, long contentLength) {
        var locked = blobUploadDao.lockUpload(id, name);
        if (!locked) {
            throw new NotFoundException("blob upload unknown to registry or could not be locked", BLOB_UPLOAD_UNKNOWN);
        }

        try {
            if (contentLength > 0) {
                uploadBlobChunkOperation.uploadChunk(id, body, range, contentLength);
            }

            String actualDigest;
            try {
                actualDigest = blobUploader.computeDigest(id);
            } catch (Exception e) {
                throw new InternalServerException("could not calculate actual digest", e);
            }

            if (!digest.equals(actualDigest)) {
                throw new InvalidRequestException("provided digest did not match uploaded content", DIGEST_INVALID);
            }

            long size;
            try {
                size = blobUploader.getSize(id);
            } catch (Exception e) {
                throw new InternalServerException("could not get blob size", e);
            }

            blobUploadDao.completeUpload(id, digest, size);
        } catch (Exception e) {
            blobUploadDao.updateStatus(id, BlobUpload.UploadStatus.FAILED);
            throw e;
        }
    }
}
