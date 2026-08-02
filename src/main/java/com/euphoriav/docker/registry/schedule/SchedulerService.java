package com.euphoriav.docker.registry.schedule;

import com.euphoriav.docker.registry.aop.annotation.Log;
import com.euphoriav.docker.registry.logic.blob.ClearOutdatedUploadsOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerService {

    private final ClearOutdatedUploadsOperation clearOutdatedUploadsOperation;

    @Log
    @Scheduled(cron = "${app.clear-uploads.cron}")
    public void clearOutdatedUploads() {
        clearOutdatedUploadsOperation.activate();
    }
}
