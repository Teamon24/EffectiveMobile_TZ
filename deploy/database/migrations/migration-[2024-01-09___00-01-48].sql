-- liquibase formatted sql

-- changeset teamo:1704744107691-1
ALTER TABLE public.comment ADD created_at TIMESTAMP(6) WITHOUT TIME ZONE;

-- changeset teamo:1704744107691-2
ALTER TABLE public.roles ADD created_at TIMESTAMP(6) WITHOUT TIME ZONE;

-- changeset teamo:1704744107691-3
ALTER TABLE public.tasks ADD created_at TIMESTAMP(6) WITHOUT TIME ZONE;

-- changeset teamo:1704744107691-4
ALTER TABLE public.users ADD created_at TIMESTAMP(6) WITHOUT TIME ZONE;

