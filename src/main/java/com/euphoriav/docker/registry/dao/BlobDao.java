package com.euphoriav.docker.registry.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BlobDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void create(String repository, String digest, long size) {
        //language=PostgreSQL
        var sql = "insert into registry.blob(repository, digest, size) values (:repository, :digest, :size)";
        var params = Map.of("repository", repository, "digest", digest, "size", size);
        jdbcTemplate.update(sql, params);
        log.info("Inserted blob with repository = {} and digest = {} and size = {}", repository, digest, size);
    }
}
