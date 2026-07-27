package com.euphoriav.docker.registry.dao;

import com.euphoriav.docker.registry.model.BlobUpload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BlobUploadDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void insert(UUID id, String repository) {
        //language=PostgreSQL
        var sql = "insert into registry.blob_upload(id, repository, status) values (:id, :repository, :status::upload_status)";
        var params = Map.of("id", id, "repository", repository, "status", BlobUpload.UploadStatus.IN_PROGRESS.toString());
        jdbcTemplate.update(sql, params);
        log.info("Inserted blob_upload with id = {} and repository = {}", id, repository);
    }

    public void updateBytesReceived(UUID id, String repository, long bytesUploaded) {
        //language=PostgreSQL
        var sql = "update registry.blob_upload set bytes_received = bytes_received + :bytesUploaded where id = :id and repository = :repository";
        var params = Map.of("id", id, "repository", repository, "bytesUploaded", bytesUploaded);
        jdbcTemplate.update(sql, params);
        log.info("Updated blob_upload with id = {} and repository = {} and bytesUploaded = {}", id, repository, bytesUploaded);
    }

    public Optional<BlobUpload> findUploadInProgress(UUID id, String repository) {
        //language=PostgreSQL
        var sql = "select * from registry.blob_upload where id = :id and repository = :repository and status = :status::upload_status for update";
        var params = Map.of("id", id, "repository", repository, "status", BlobUpload.UploadStatus.IN_PROGRESS.toString());
        var result = jdbcTemplate.query(sql, params, BeanPropertyRowMapper.newInstance(BlobUpload.class));
        log.info("Selected blob_upload with id = {} and repository = {}", id, repository);
        return result.stream().findFirst();
    }
}
