package com.euphoriav.docker.registry.dao;

import com.euphoriav.docker.registry.aop.annotation.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BlobDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Log
    public void create(String repository, String digest, long size) {
        //language=PostgreSQL
        var sql = "insert into registry.blob(repository, digest, size) values (:repository, :digest, :size)";
        var params = Map.of("repository", repository, "digest", digest, "size", size);
        jdbcTemplate.update(sql, params);
    }
}
