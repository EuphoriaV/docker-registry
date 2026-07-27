--liquibase formatted sql
--changeset EuphoriaV:create_blob_upload_table

create type upload_status as enum ('IN_PROGRESS', 'COMPLETED');

create table if not exists registry.blob_upload
(
    id             uuid primary key,
    repository     varchar       not null,
    status         upload_status not null,
    bytes_received bigint        not null default 0,
    created_at     timestamp     not null default now()
);