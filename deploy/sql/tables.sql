create table comment (
    creation_date timestamp(6) not null,
    id            bigserial    not null,
    task_id       bigint       not null,
    user_id       bigint       not null,
    content       varchar(255),
    primary key (id)
);

create table tasks (
    id          bigserial   not null,
    priority    smallint    not null check (priority between 0 and 2),
    creator_id  bigint      not null,
    executor_id bigint,
    status      varchar(20) not null check (status in ('NEW', 'ASSIGNED', 'EXECUTING', 'DONE', 'PENDING')),
    content     varchar(255),
    primary key (id)
);

create table users (
    id       bigserial not null,
    email    varchar(255),
    password varchar(255),
    username varchar(255),
    primary key (id),
    unique (username),
    unique (email)
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