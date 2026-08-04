--liquibase formatted sql
--changeset EuphoriaV:create_registry_lock_table

create table if not exists registry_lock
(
    lock_key      varchar(36) not null,
    region        varchar(36) not null,
    client_id     varchar(36),
    created_date  timestamp   not null,
    primary key (lock_key, region)
);