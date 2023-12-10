CREATE DATABASE task_management_sys;

create table IF NOT EXISTS users
(
    id       bigserial not null,
    email    varchar(255),
    password varchar(255),
    username varchar(255),
    primary key (id),
    unique (username),
    unique (email)
);

create table IF NOT EXISTS users_roles (
    role_id bigint not null,
    user_id bigint not null
);

create table IF NOT EXISTS roles (
    id   bigserial not null,
    name varchar(20) check (name in ('USER', 'CREATOR', 'EXECUTOR')),
    primary key (id)
);

create table IF NOT EXISTS tasks (
    priority    smallint    not null check (priority between 0 and 2),
    creator_id  bigint      not null,
    executor_id bigint,
    id          bigserial   not null,
    status      varchar(20) not null check (status in ('NEW', 'ASSIGNED', 'EXECUTING', 'DONE')),
    content     varchar(255),
    primary key (id)
);

create table IF NOT EXISTS comment (
    creation_date timestamp(6) not null,
    id            bigserial    not null,
    task_id       bigint       not null,
    user_id       bigint       not null unique,
    content       varchar(255),
    primary key (id)
);

alter table if exists comment
    add constraint FKfwc7uv5cfkqi3dnb2t20rnurc
        foreign key (task_id)
            references tasks;

alter table if exists comment
    add constraint FKqm52p1v3o13hy268he0wcngr5
        foreign key (user_id)
            references users;

alter table if exists tasks
    add constraint FKt1ph5sat39g9lpa4g5kl46tbv
        foreign key (creator_id)
            references users;

alter table if exists tasks
    add constraint FKbrg922bkqn5m7212jsqjg6ioe
        foreign key (executor_id)
            references users;

alter table if exists users_roles
    add constraint FKj6m8fwv7oqv74fcehir1a9ffy
        foreign key (role_id)
            references roles;

alter table if exists users_roles
    add constraint FK2o0jvgh89lemvvo17cbqvdxaa
        foreign key (user_id)
            references users;

insert into roles (name) values ('USER');
insert into roles (name) values ('CREATOR');
insert into roles (name) values ('EXECUTOR');
