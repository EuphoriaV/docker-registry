package com.euphoriav.docker.registry.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class RepositoryDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public boolean existsByName(String name) {
        //language=PostgreSQL
        var sql = "select 1 from registry.repository where name = :name";
        var params = Map.of("name", name);
        return jdbcTemplate.query(sql, params, ResultSet::next);
    }
}
