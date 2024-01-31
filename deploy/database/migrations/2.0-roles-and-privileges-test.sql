CREATE TEMP TABLE privileges(id, name, parent_id) AS (
    SELECT  1 , 'PRIVILEGE_1'         , CAST(NULL AS int8) UNION ALL

    SELECT  2 , 'PRIVILEGE_2'         , CAST(NULL AS int8) UNION ALL
    SELECT  3 , 'PRIVILEGE_2.1'       , 2                  UNION ALL
    SELECT  4 , 'PRIVILEGE_2.1.1'     , 3                  UNION ALL
    SELECT  5 , 'PRIVILEGE_2.1.2'     , 3                  UNION ALL
    SELECT  6 , 'PRIVILEGE_2.1.2.1'   , 5                  UNION ALL
    SELECT  7 , 'PRIVILEGE_2.1.2.1.1' , 6                  UNION ALL
    SELECT  8 , 'PRIVILEGE_2.2'       , 2                  UNION ALL
    SELECT  9 , 'PRIVILEGE_2.2.1'     , 8                  UNION ALL
    SELECT 10 , 'PRIVILEGE_2.2.2'     , 8                  UNION ALL
    SELECT 11 , 'PRIVILEGE_2.2.3'     , 8                  UNION ALL

    SELECT 12 , 'PRIVILEGE_3'         , CAST(NULL AS int8) UNION ALL
    SELECT 13 , 'PRIVILEGE_3.1'       , 12                 UNION ALL
    SELECT 14 , 'PRIVILEGE_3.2'       , 12                 UNION ALL
    SELECT 15 , 'PRIVILEGE_3.2.1'     , 14                 UNION ALL
    SELECT 16 , 'PRIVILEGE_3.2.2'     , 14                 UNION ALL
    SELECT 17 , 'PRIVILEGE_4'         , CAST(NULL AS int8) UNION ALL
    SELECT 18 , 'PRIVILEGE_4.1'       , 17                 UNION ALL
    SELECT 19 , 'PRIVILEGE_4.2'       , 17                 UNION ALL
    SELECT 20 , 'PRIVILEGE_4.2.1'     , 19                 UNION ALL
    SELECT 21 , 'PRIVILEGE_4.2.2'     , 19
);

CREATE TEMP TABLE names(creator, executor, spectator) AS (SELECT 'creator', 'executor', 'spectator');

CREATE TEMP TABLE roles(id, name) AS (
    SELECT 1, (SELECT names.creator FROM names) UNION ALL
    SELECT 2, (SELECT names.executor FROM names) UNION ALL
    SELECT 3, (SELECT names.spectator FROM names)
);

CREATE TEMP TABLE creator(id) AS (SELECT roles.* FROM roles, names WHERE name = names.creator);
CREATE TEMP TABLE executor(id) AS (SELECT roles.* FROM roles, names WHERE name = names.executor);
CREATE TEMP TABLE spectator(id) AS (SELECT roles.id FROM roles, names WHERE name = names.spectator);

CREATE TEMP TABLE privileges_1  (id, name) AS (SELECT * FROM privileges WHERE name = 'PRIVILEGES_1');
CREATE TEMP TABLE privileges_2  (id, name) AS (SELECT * FROM privileges WHERE name = 'PRIVILEGES_2');
CREATE TEMP TABLE privileges_3  (id, name) AS (SELECT * FROM privileges WHERE name = 'PRIVILEGES_3');
CREATE TEMP TABLE privileges_32 (id, name) AS (SELECT * FROM privileges WHERE name = 'PRIVILEGES_3.2');
CREATE TEMP TABLE privileges_322(id, name) AS (SELECT * FROM privileges WHERE name = 'PRIVILEGES_3.2.2');

CREATE TEMP TABLE roles_privileges(role_id, privilege_id) AS (
    SELECT r.id, p.id FROM creator r, privileges p WHERE p.name = 'PRIVILEGE_1' UNION ALL
    SELECT r.id, p.id FROM creator r, privileges p WHERE p.name = 'PRIVILEGE_2' UNION ALL
    SELECT r.id, p.id FROM creator r, privileges p WHERE p.name = 'PRIVILEGE_3.2' UNION ALL
    SELECT r.id, p.id FROM creator r, privileges p WHERE p.name = 'PRIVILEGE_4.2.2' UNION ALL

    SELECT r.id, p.id FROM executor r, privileges p WHERE p.name = 'PRIVILEGE_2.2' UNION ALL
    SELECT r.id, p.id FROM executor r, privileges p WHERE p.name = 'PRIVILEGE_3.2.2' UNION ALL
    SELECT r.id, p.id FROM executor r, privileges p WHERE p.name = 'PRIVILEGE_4.2.1' UNION ALL

    SELECT r.id, p.id FROM spectator r, privileges p WHERE p.name = 'PRIVILEGE_2.2'
);

CREATE OR REPLACE FUNCTION pg_temp.test_find_privileges_branch(root_privilege_id int2, expectedCount int2)
    RETURNS table(r text)
    LANGUAGE plpgsql
AS
$$
BEGIN
    RETURN QUERY
    WITH RECURSIVE tree(name, id, parent_id) as (
        SELECT p.name, p.id, parent_id
        FROM privileges p
                 LEFT JOIN roles_privileges rp ON p.id = rp.privilege_id
                 LEFT JOIN roles r ON r.id = rp.role_id
        WHERE p.id = root_privilege_id
        UNION ALL
        SELECT child.name, child.id, child.parent_id
        FROM privileges child
                 JOIN tree ON child.parent_id = tree.id
    )

    SELECT 'test'
    UNION ALL
    SELECT format('find privileges by root privilege id = %s', root_privilege_id)
    UNION ALL
    SELECT ''
    UNION ALL
    SELECT format('%-15s %-15s %-30s', 'parent_id', 'id', 'name')
    UNION ALL
    (SELECT format('%-15s %-15s %-30s', parent_id, id, name) FROM (SELECT * FROM tree) r ORDER BY r.name)
    UNION ALL
    SELECT ''
    UNION ALL
    SELECT 'assertion:'
    UNION ALL
    SELECT format(
                   '%s %s %s',
                   CASE WHEN true THEN expectedCount ELSE expectedCount END,
                   CASE WHEN expectedCount = count(*) THEN '=' ELSE '!=' END,
                   count(*)
               )
    FROM (SELECT * FROM tree);
END
$$;

CREATE OR REPLACE FUNCTION pg_temp.test_find_privileges_by_role(role text, expectedCount int2)
    RETURNS table(r text)
    LANGUAGE plpgsql
AS
$$
BEGIN
    RETURN QUERY
        WITH RECURSIVE
        tree(name, id, parent_id) as (
            SELECT parent.name, parent.id, parent_id
            FROM privileges parent
                     LEFT JOIN roles_privileges rp on rp.privilege_id = parent.id
                     LEFT JOIN roles r on r.id = rp.role_id
            WHERE r.name in (role)
            UNION
            SELECT child.name, child.id, child.parent_id
            FROM privileges child
                     JOIN tree ON child.parent_id = tree.id
        )

        SELECT 'test'
        UNION ALL
        SELECT format('find privileges by role = %s', role)
        UNION ALL
        SELECT ''
        UNION ALL
        SELECT format('%-15s %-15s %-30s %s', 'parent_id', 'id', 'name', 'role')
        UNION ALL
        (SELECT format('%-15s %-15s %-30s %s', parent_id, id, name, role) FROM (SELECT * FROM tree) r ORDER BY r.name)
        UNION ALL
        SELECT ''
        UNION ALL
        SELECT 'assertion:'
        UNION ALL
        SELECT format(
                       'expected %s %s %s actual',
                       CASE WHEN true THEN expectedCount ELSE expectedCount END,
                       CASE WHEN expectedCount = count(*) THEN '=' ELSE '!=' END,
                       count(*)
                   )
        FROM (SELECT * FROM tree);
END $$;

SELECT pg_temp.test_find_privileges_by_role('creator',  15::int2);
SELECT pg_temp.test_find_privileges_by_role('executor',  6::int2);
SELECT pg_temp.test_find_privileges_by_role('spectator', 4::int2);

SELECT pg_temp.test_find_privileges_branch(1::int2, 1::int2);
SELECT pg_temp.test_find_privileges_branch(2::int2, 10::int2);
SELECT pg_temp.test_find_privileges_branch(3::int2, 5::int2);
SELECT pg_temp.test_find_privileges_branch(4::int2, 1::int2);
SELECT pg_temp.test_find_privileges_branch(5::int2, 3::int2);
SELECT pg_temp.test_find_privileges_branch(6::int2, 2::int2);
SELECT pg_temp.test_find_privileges_branch(7::int2, 1::int2);
SELECT pg_temp.test_find_privileges_branch(8::int2, 4::int2);
SELECT pg_temp.test_find_privileges_branch(9::int2, 1::int2);
SELECT pg_temp.test_find_privileges_branch(10::int2, 1::int2);
SELECT pg_temp.test_find_privileges_branch(11::int2, 1::int2);
SELECT pg_temp.test_find_privileges_branch(12::int2, 5::int2);
SELECT pg_temp.test_find_privileges_branch(13::int2, 1::int2);
SELECT pg_temp.test_find_privileges_branch(14::int2, 3::int2);
SELECT pg_temp.test_find_privileges_branch(15::int2, 1::int2);
SELECT pg_temp.test_find_privileges_branch(16::int2, 1::int2);
SELECT pg_temp.test_find_privileges_branch(17::int2, 5::int2);
SELECT pg_temp.test_find_privileges_branch(18::int2, 1::int2);
SELECT pg_temp.test_find_privileges_branch(19::int2, 3::int2);
SELECT pg_temp.test_find_privileges_branch(20::int2, 1::int2);
SELECT pg_temp.test_find_privileges_branch(21::int2, 1::int2);

DROP TABLE IF EXISTS privileges;
DROP TABLE IF EXISTS names;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS creator;
DROP TABLE IF EXISTS executor;
DROP TABLE IF EXISTS spectator;
DROP TABLE IF EXISTS privileges_1;
DROP TABLE IF EXISTS privileges_2;
DROP TABLE IF EXISTS privileges_3;
DROP TABLE IF EXISTS privileges_32;
DROP TABLE IF EXISTS privileges_322;
DROP TABLE IF EXISTS roles_privileges;

DROP FUNCTION IF EXISTS pg_temp.test_find_privileges_by_role(text, int2);
DROP FUNCTION IF EXISTS pg_temp.test_find_privileges_branch(int2, int2);
