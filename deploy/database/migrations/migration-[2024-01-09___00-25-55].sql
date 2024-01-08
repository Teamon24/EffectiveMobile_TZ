-- liquibase formatted sql

-- changeset teamo:1704745554947-1
ALTER TABLE public.comment ADD updated_at TIMESTAMP(6) WITHOUT TIME ZONE;

-- changeset teamo:1704745554947-2
ALTER TABLE public.roles ADD updated_at TIMESTAMP(6) WITHOUT TIME ZONE;

-- changeset teamo:1704745554947-3
ALTER TABLE public.tasks ADD updated_at TIMESTAMP(6) WITHOUT TIME ZONE;

-- changeset teamo:1704745554947-4
ALTER TABLE public.users ADD updated_at TIMESTAMP(6) WITHOUT TIME ZONE;

