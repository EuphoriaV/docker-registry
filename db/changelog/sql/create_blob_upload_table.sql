--liquibase formatted sql
--changeset EuphoriaV:create_blob_upload_table

create table if not exists registry.blob_upload
(
    id             uuid primary key,
    repository     varchar   not null,
    bytes_received bigint    not null default 0,
    created_at     timestamp not null default now()
);