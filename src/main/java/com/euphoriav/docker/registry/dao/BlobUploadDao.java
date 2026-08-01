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
        var sql = "insert into registry.blob_upload(id, repository) values (:id, :repository)";
        var params = Map.of("id", id, "repository", repository);
        jdbcTemplate.update(sql, params);
        log.info("Inserted blob_upload with id = {} and repository = {}", id, repository);
    }

    public Optional<BlobUpload> find(UUID id, String repository) {
        //language=PostgreSQL
        var sql = "select * from registry.blob_upload where id = :id and repository = :repository";
        var params = Map.of("id", id, "repository", repository);
        var result = jdbcTemplate.query(sql, params, BeanPropertyRowMapper.newInstance(BlobUpload.class));
        log.info("selected blob_upload with id = {} and repository = {}", id, repository);
        return result.stream().findFirst();
    }

    public void updateBytesReceived(UUID id, long bytesReceived) {
        //language=PostgreSQL
        var sql = "update registry.blob_upload set bytes_received = bytes_received + :bytesReceived where id = :id";
        var params = Map.of("id", id, "bytesReceived", bytesReceived);
        jdbcTemplate.update(sql, params);
        log.info("Updated blob_upload with id = {} and bytesReceived = {}", id, bytesReceived);
    }

    public void delete(UUID id) {
        //language=PostgreSQL
        var sql = "delete from registry.blob_upload where id = :id";
        var params = Map.of("id", id);
        jdbcTemplate.update(sql, params);
        log.info("Deleted blob_upload with id = {}", id);
    }
}
