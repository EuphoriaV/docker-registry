--liquibase formatted sql
--changeset EuphoriaV:create_blob_upload_table

create type upload_status as enum ('IDLE', 'IN_PROGRESS', 'COMPLETED', 'FAILED');

create table if not exists registry.blob_upload
(
    id         uuid primary key,
    repository varchar       not null,
    status     upload_status not null,
    size       bigint,
    digest     varchar,
    created_at timestamp     not null default now()
);