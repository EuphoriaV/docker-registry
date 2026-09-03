--liquibase formatted sql
--changeset EuphoriaV:create_manifest_table

create table if not exists registry.manifest
(
    id         bigserial primary key,
    repository varchar   not null,
    digest     varchar   not null,
    data       bytea     not null,
    media_type varchar   not null,
    size       bigint    not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),
    unique (repository, digest)
);