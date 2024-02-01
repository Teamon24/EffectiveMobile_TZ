CREATE DATABASE task_management_system;

create table comment
(
    id            bigserial    not null,
    content       varchar(255),
    creation_date timestamp(6) not null,
    task_id       bigint       not null,
    user_id       bigint       not null,
    primary key (id)
);

create table tasks
(
    id          bigserial   not null,
    content     varchar(255),
    priority    smallint    not null,
    status      varchar(20) not null,
    creator_id  bigint      not null,
    executor_id bigint,
    primary key (id)
);

create table users
(
    id       bigserial not null,
    email    varchar(255),
    password varchar(255),
    username varchar(255),
    primary key (id)
);

alter table if exists users
    drop constraint if exists UKr43af9ap4edm43mmtq01oddj6;

alter table if exists users
    add constraint UKr43af9ap4edm43mmtq01oddj6 unique (username);

alter table if exists users
    drop constraint if exists UK6dotkott2kjsp8vw4d0m25fb7;

alter table if exists users
    add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email);

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
