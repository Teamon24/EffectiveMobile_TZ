-- liquibase formatted sql

-- changeset teamo:4
ALTER TABLE public.comment ADD          deleted_at TIMESTAMP(6) WITHOUT TIME ZONE;
ALTER TABLE public.tasks   ADD          deleted_at TIMESTAMP(6) WITHOUT TIME ZONE;
ALTER TABLE public.users   ADD          deleted_at TIMESTAMP(6) WITHOUT TIME ZONE;
ALTER TABLE public.comment ALTER COLUMN created_at SET NOT NULL;
ALTER TABLE public.tasks   ALTER COLUMN created_at SET NOT NULL;
ALTER TABLE public.users   ALTER COLUMN created_at SET NOT NULL;
ALTER TABLE public.comment ALTER COLUMN updated_at SET NOT NULL;
ALTER TABLE public.tasks   ALTER COLUMN updated_at SET NOT NULL;
ALTER TABLE public.users   ALTER COLUMN updated_at SET NOT NULL;
-- rollback changesetId:4 changesetAuthor:teamo changesetPath:deploy/database/migrations/src/down/migration-4-[1-db-tag]-[2024-01-09___16-05-51]-down.sql
