package com.euphoriav.docker.registry.dao;


import com.euphoriav.docker.registry.aop.annotation.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class TagDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Log
    public void create(String repository, String digest, String tag) {
        //language=PostgreSQL
        var sql = "insert into registry.tag(repository, digest, tag, updated_at) values (:repository, :digest, :tag, now()) on conflict (repository, tag) do update set digest = :digest, updated_at = now()";
        var params = Map.of("repository", repository, "digest", digest, "tag", tag);
        jdbcTemplate.update(sql, params);
    }
}