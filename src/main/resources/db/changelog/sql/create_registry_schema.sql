--liquibase formatted sql
--changeset EuphoriaV:create_repository_table
create schema if not exists registry;