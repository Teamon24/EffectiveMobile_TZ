-- liquibase formatted sql

-- changeset teamo:2
ALTER TABLE public.comment ADD created_at TIMESTAMP(6) WITHOUT TIME ZONE;
ALTER TABLE public.tasks ADD created_at TIMESTAMP(6) WITHOUT TIME ZONE;
ALTER TABLE public.users ADD created_at TIMESTAMP(6) WITHOUT TIME ZONE;
-- rollback changesetId:2 changesetAuthor:teamo changesetPath:deploy/database/migrations/src/down/migration-2-[1-db-tag]-[2024-01-09___00-01-48]-down.sql

