create table roles
(
    id   bigserial   not null,
    name varchar(40) not null,
    primary key (id)
);

alter table if exists roles
    add constraint UKc78vwrojm1ffh7ifkvaf96qfe
        unique (name);

create table users_roles
(
    user_id bigint not null,
    role_id bigint not null
);

alter table if exists users_roles
    add constraint FKj6m8fwv7oqv74fcehir1a9ffy
        foreign key (role_id)
            references roles;

alter table if exists users_roles
    add constraint FK2o0jvgh89lemvvo17cbqvdxaa
        foreign key (user_id)
            references users;

create table privileges
(
    id        bigserial not null,
    name      varchar(255),
    parent_id bigint,
    primary key (id)
);

alter table if exists privileges
    add constraint UK2jbaynrvcc7umsy5dltc90t5n
        unique (name);

alter table if exists privileges
    add constraint FK7tlj9utcydcr523b3qp6lui19
        foreign key (parent_id)
            references privileges;

alter table if exists privileges
    add constraint FK1mdqqlj91nvaav50bmnscn25y
        foreign key (id)
            references privileges;

create table roles_privileges
(
    role_id      bigint not null,
    privilege_id bigint not null
);

alter table if exists roles_privileges
    add constraint FK5duhoc7rwt8h06avv41o41cfy
        foreign key (privilege_id)
            references privileges;

alter table if exists roles_privileges
    add constraint FK629oqwrudgp5u7tewl07ayugj
        foreign key (role_id)
            references roles;







