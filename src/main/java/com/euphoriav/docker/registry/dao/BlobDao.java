package com.euphoriav.docker.registry.dao;

import com.euphoriav.docker.registry.aop.annotation.Log;
import com.euphoriav.docker.registry.model.Blob;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BlobDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Log(isDebug = true)
    public void create(String repository, String digest, long size, UUID id) {
        //language=PostgreSQL
        var sql = "insert into registry.blob(repository, digest, size, filename) values (:repository, :digest, :size, :filename)";
        var params = Map.of("repository", repository, "digest", digest, "size", size, "filename", id.toString());
        jdbcTemplate.update(sql, params);
    }

    @Log(isDebug = true)
    public Optional<Blob> find(String digest, String repository) {
        //language=PostgreSQL
        var sql = "select * from registry.blob where digest = :digest and repository = :repository for share";
        var params = Map.of("digest", digest, "repository", repository);
        return jdbcTemplate.query(sql, params, BeanPropertyRowMapper.newInstance(Blob.class)).stream().findFirst();
    }

    @Log(isDebug = true)
    public Optional<Blob> findForUpdate(String digest, String repository) {
        //language=PostgreSQL
        var sql = "select * from registry.blob where digest = :digest and repository = :repository for update";
        var params = Map.of("digest", digest, "repository", repository);
        return jdbcTemplate.query(sql, params, BeanPropertyRowMapper.newInstance(Blob.class)).stream().findFirst();
    }

    @Log(isDebug = true)
    public void delete(String repository, String digest) {
        //language=PostgreSQL
        var sql = "delete from registry.blob where repository = :repository and digest = :digest";
        var params = Map.of("repository", repository, "digest", digest);
        jdbcTemplate.update(sql, params);
    }
}
