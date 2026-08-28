--liquibase formatted sql
--changeset EuphoriaV:create_tag_table

create table if not exists registry.tag
(
    id         bigserial primary key,
    repository varchar   not null,
    tag        varchar   not null,
    digest     varchar   not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),
    unique (repository, tag)
);