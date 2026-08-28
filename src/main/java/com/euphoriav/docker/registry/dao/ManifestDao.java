package com.euphoriav.docker.registry.dao;


import com.euphoriav.docker.registry.aop.annotation.Log;
import com.euphoriav.docker.registry.model.BlobUpload;
import com.euphoriav.docker.registry.model.Manifest;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

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

    @Log
    public Optional<Manifest> findByDigest(String repository, String digest) {
        //language=PostgreSQL
        var sql = "select * from registry.manifest where repository = :repository and digest = :digest";
        var params = Map.of("repository", repository, "digest", digest);
        return jdbcTemplate.query(sql, params, BeanPropertyRowMapper.newInstance(Manifest.class)).stream().findFirst();
    }

    @Log
    public Optional<Manifest> findByTag(String repository, String tag) {
        //language=PostgreSQL
        var sql = "select m.id, m.repository, m.digest, m.data, m.media_type, m.size from registry.manifest m join registry.tag t using (repository, digest) where t.repository = :repository and t.tag = :tag";
        var params = Map.of("repository", repository, "tag", tag);
        return jdbcTemplate.query(sql, params, BeanPropertyRowMapper.newInstance(Manifest.class)).stream().findFirst();
    }
}