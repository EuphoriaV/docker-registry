--liquibase formatted sql
--changeset EuphoriaV:create_registry_schema

create schema if not exists registry;