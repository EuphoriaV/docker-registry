--liquibase formatted sql
--changeset EuphoriaV:create_blob_table

create table if not exists registry.blob
(
    id         bigserial primary key,
    repository varchar   not null,
    digest     varchar   not null unique,
    size       bigint    not null,
    filename   varchar   not null,
    created_at timestamp not null default now()
);