package com.euphoriav.docker.registry.logic.helper;

import com.euphoriav.docker.registry.exception.InternalServerException;
import com.euphoriav.docker.registry.exception.InvalidRangeException;
import com.euphoriav.docker.registry.logic.blob.upload.BlobUploader;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UploadChunkHelper {

    private final BlobUploader blobUploader;

    public void compareRangeAndUploadChunk(Resource body, String range, long startRange, long endRange, UUID id) {
        var expectedRange = "%d-%d".formatted(startRange, endRange);
        if (StringUtils.isNotEmpty(range) && !expectedRange.equals(range)) {
            throw new InvalidRangeException("range is out of order");
        }

        try {
            blobUploader.uploadChunk(id, body.getInputStream(), startRange);
        } catch (Exception e) {
            throw new InternalServerException("could not upload chunk", e);
        }
    }
}
