package com.euphoriav.docker.registry.dao;


import com.euphoriav.docker.registry.aop.annotation.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ManifestDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Log
    public void create(String repository, String digest, byte[] data, String contentType) {
        //language=PostgreSQL
        var sql = "insert into registry.manifest(repository, digest, data, media_type, size, updated_at) values (:repository, :digest, :data, :mediaType, :size, now()) on conflict (repository, digest) do nothing";
        var params = Map.of("repository", repository, "digest", digest, "data", data, "mediaType", contentType, "size", data.length);
        jdbcTemplate.update(sql, params);
    }
}