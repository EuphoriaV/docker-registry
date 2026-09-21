--liquibase formatted sql
--changeset EuphoriaV:create_user_table

create table if not exists auth.user
(
    id            bigserial primary key,
    login         varchar   not null unique,
    password_hash varchar   not null,
    created_at    timestamp not null default now()
);