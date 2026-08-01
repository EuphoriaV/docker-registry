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

    @Bean
    public LockRepository lockRepository(DataSource dataSource) {
        var repository = new DefaultLockRepository(dataSource);
        repository.setPrefix("registry_");
        repository.setRegion("docker-registry");
        return repository;
    }

    @Bean
    public LockRegistry<?> lockRegistry() {
        return new JdbcLockRegistry(lockRepository(null));
    }
}
