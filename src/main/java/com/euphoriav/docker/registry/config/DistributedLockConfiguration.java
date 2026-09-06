package com.euphoriav.docker.registry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.integration.jdbc.lock.LockRepository;
import org.springframework.integration.support.locks.LockRegistry;

import javax.sql.DataSource;

@Configuration
public class DistributedLockConfiguration {

    private static final int TTL_MS = 10 * 60 * 1000;

    @Bean
    public LockRepository lockRepository(DataSource dataSource) {
        var repository = new DefaultLockRepository(dataSource);
        repository.setPrefix("registry_");
        repository.setRegion("docker-registry");
        repository.setTimeToLive(TTL_MS);
        return repository;
    }

    @Bean
    public LockRegistry lockRegistry(LockRepository lockRepository) {
        return new JdbcLockRegistry(lockRepository);
    }
}
