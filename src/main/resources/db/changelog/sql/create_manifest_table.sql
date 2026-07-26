--liquibase formatted sql
--changeset EuphoriaV:create_manifest_table

create table if not exists registry.manifest
(
    id            serial primary key,
    repository_id integer      not null references registry.repository (id),
    name          varchar      not null,
    digest        varchar(100) not null,
    data          bytea        not null,
    created_at    timestamp    not null default now(),
    unique (repository_id, digest)
);