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

    public void tryInLock(String key, Runnable runnable) {
        tryInLock(key, () -> {
            runnable.run();
            return null;
        });
    }

    public <T> T tryInLock(String key, Supplier<T> supplier) {
        var lock = lockRegistry.obtain(key);
        try {
            if (lock.tryLock(LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                try {
                    log.info("obtained lock with key = {}", key);
                    return supplier.get();
                } finally {
                    lock.unlock();
                    log.info("released lock with key = {}", key);
                }
            } else {
                throw new InvalidRequestException("could not obtain lock with key = %s".formatted(key), ErrorResponse.ErrorCode.BLOB_UPLOAD_UNKNOWN);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
