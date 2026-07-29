package com.euphoriav.docker.registry.dao;

import com.euphoriav.docker.registry.model.BlobUpload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BlobUploadDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void insert(UUID id, String repository) {
        //language=PostgreSQL
        var sql = "insert into registry.blob_upload(id, repository, status) values (:id, :repository, 'IDLE'::upload_status)";
        var params = Map.of("id", id, "repository", repository);
        jdbcTemplate.update(sql, params);
        log.info("Inserted blob_upload with id = {} and repository = {}", id, repository);
    }

    public boolean lockUpload(UUID id, String repository) {
        //language=PostgreSQL
        var sql = "update registry.blob_upload set status = 'IN_PROGRESS'::upload_status where id = :id and repository = :repository and status = 'IDLE'::upload_status";
        var params = Map.of("id", id, "repository", repository);
        var res = jdbcTemplate.update(sql, params);
        log.info("Updated blob_upload with id = {} and repository = {}", id, repository);
        return res > 0;
    }

    public void updateStatus(UUID id, BlobUpload.UploadStatus status) {
        //language=PostgreSQL
        var sql = "update registry.blob_upload set status = :status::upload_status where id = :id";
        var params = Map.of("id", id, "status", status.toString());
        jdbcTemplate.update(sql, params);
        log.info("Updated blob_upload with id = {}", id);
    }

    public void completeUpload(UUID id, String digest, long size) {
        //language=PostgreSQL
        var sql = "update registry.blob_upload set status = 'COMPLETED'::upload_status, size = :size, digest = :digest where id = :id";
        var params = Map.of("id", id, "size", size, "digest", digest);
        jdbcTemplate.update(sql, params);
        log.info("Updated blob_upload with id = {} and digest = {} and size = {}", id, digest, size);
    }
}
