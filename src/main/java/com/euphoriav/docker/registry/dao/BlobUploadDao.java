package com.euphoriav.docker.registry.dao;

import com.euphoriav.docker.registry.aop.annotation.Log;
import com.euphoriav.docker.registry.model.BlobUpload;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BlobUploadDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Log
    public void insert(UUID id, String repository) {
        //language=PostgreSQL
        var sql = "insert into registry.blob_upload(id, repository) values (:id, :repository)";
        var params = Map.of("id", id, "repository", repository);
        jdbcTemplate.update(sql, params);
    }

    @Log
    public Optional<BlobUpload> find(UUID id, String repository) {
        //language=PostgreSQL
        var sql = "select * from registry.blob_upload where id = :id and repository = :repository";
        var params = Map.of("id", id, "repository", repository);
        return jdbcTemplate.query(sql, params, BeanPropertyRowMapper.newInstance(BlobUpload.class)).stream().findFirst();
    }

    @Log
    public void updateBytesReceived(UUID id, long bytesReceived) {
        //language=PostgreSQL
        var sql = "update registry.blob_upload set bytes_received = bytes_received + :bytesReceived where id = :id";
        var params = Map.of("id", id, "bytesReceived", bytesReceived);
        jdbcTemplate.update(sql, params);
    }

    @Log
    public void delete(UUID id) {
        //language=PostgreSQL
        var sql = "delete from registry.blob_upload where id = :id";
        var params = Map.of("id", id);
        jdbcTemplate.update(sql, params);
    }
}
