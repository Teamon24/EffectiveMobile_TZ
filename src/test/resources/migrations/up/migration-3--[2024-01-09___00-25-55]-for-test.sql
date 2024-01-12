-- liquibase formatted sql

-- changeset teamo:3
ALTER TABLE public.comment ADD updated_at TIMESTAMP(6) WITHOUT TIME ZONE;
ALTER TABLE public.tasks ADD updated_at TIMESTAMP(6) WITHOUT TIME ZONE;
ALTER TABLE public.users ADD updated_at TIMESTAMP(6) WITHOUT TIME ZONE;
-- rollback changesetId:3 changesetAuthor:teamo changesetPath:deploy/database/migrations/src/down/migration-3-[1-db-tag]-[2024-01-09___00-25-55]-down.sql
