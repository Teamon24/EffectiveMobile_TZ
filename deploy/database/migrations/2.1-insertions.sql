create or replace function pg_temp.privilege_id(privilege_name varchar) returns int8
as
$$
begin
    RETURN (SELECT id FROM privileges WHERE name = privilege_name);
end;
$$
language plpgsql;

INSERT INTO roles(name) VALUES ('creator'), ('executor');

INSERT INTO privileges(name, parent_id)
VALUES ('TASK_STATUS_CHANGE', NULL);

WITH status_change(id) AS (
    SELECT id FROM privileges WHERE name = 'TASK_STATUS_CHANGE'
)
INSERT INTO privileges(name, parent_id)
VALUES
    ('STATUS_EXECUTING'           , (SELECT id FROM status_change)),
    ('STATUS_SUSPENDING'          , (SELECT id FROM status_change)),
    ('STATUS_FINISHING'           , (SELECT id FROM status_change)),
    ('STATUS_RESUMING'            , (SELECT id FROM status_change)),
    ('STATUS_SUSPENDED_FINISHING' , (SELECT id FROM status_change)),
    ('STATUS_FINISHED_SUSPENDING' , (SELECT id FROM status_change));

WITH
     executor(id) AS (SELECT id FROM roles WHERE name = 'executor'),
     creator(id)  AS (SELECT id FROM roles WHERE name = 'creator')
INSERT INTO roles_privileges
VALUES
    ((SELECT id FROM creator) , pg_temp.privilege_id('STATUS_SUSPENDING')),
    ((SELECT id FROM creator) , pg_temp.privilege_id('STATUS_SUSPENDED_FINISHING')),
    ((SELECT id FROM creator) , pg_temp.privilege_id('STATUS_FINISHED_SUSPENDING')),
    ((SELECT id FROM executor), pg_temp.privilege_id('STATUS_EXECUTING')),
    ((SELECT id FROM executor), pg_temp.privilege_id('STATUS_SUSPENDING')),
    ((SELECT id FROM executor), pg_temp.privilege_id('STATUS_FINISHING')),
    ((SELECT id FROM executor), pg_temp.privilege_id('STATUS_RESUMING')),
    ((SELECT id FROM executor), pg_temp.privilege_id('STATUS_SUSPENDED_FINISHING')),
    ((SELECT id FROM executor), pg_temp.privilege_id('STATUS_FINISHED_SUSPENDING'));