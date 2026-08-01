package com.euphoriav.docker.registry.logic.blob.lock;

import com.euphoriav.docker.registry.dto.ErrorResponse;
import com.euphoriav.docker.registry.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class LockService {

    private static final long LOCK_TIMEOUT_SECONDS = 5;

    private final LockRegistry lockRegistry;

    public void tryInLock(UUID id, Runnable runnable) {
        tryInLock(id, () -> {
            runnable.run();
            return null;
        });
    }

    public <T> T tryInLock(UUID id, Supplier<T> supplier) {
        var lock = lockRegistry.obtain(id.toString());
        try {
            if (lock.tryLock(LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                try {
                    log.info("locked blob_upload with id = {}", id);
                    return supplier.get();
                } finally {
                    lock.unlock();
                    log.info("unlocked blob_upload with id = {}", id);
                }
            } else {
                throw new InvalidRequestException("could not lock upload with id = %s".formatted(id), ErrorResponse.ErrorCode.BLOB_UPLOAD_INVALID);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
