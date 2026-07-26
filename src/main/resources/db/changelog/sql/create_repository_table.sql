--liquibase formatted sql
--changeset EuphoriaV:create_repository_table

create table if not exists registry.repository
(
    id         serial primary key,
    name       varchar(255) unique not null,
    created_at timestamp           not null default now()
);