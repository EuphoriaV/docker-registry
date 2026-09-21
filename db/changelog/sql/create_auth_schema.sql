--liquibase formatted sql
--changeset EuphoriaV:create_auth_schema

create schema if not exists auth;