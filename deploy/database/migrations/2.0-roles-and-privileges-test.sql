WITH RECURSIVE tree(name, id, parent_id) as (
    SELECT p.name, p.id, parent_id
    FROM privileges p
             LEFT JOIN roles_privileges rp ON p.id = rp.privilege_id
             LEFT JOIN roles r ON r.id = rp.role_id
    WHERE p.id = 19
    UNION ALL
    SELECT child.name, child.id, child.parent_id
    FROM privileges child
             JOIN tree ON child.parent_id = tree.id
),
               privileges(id, name, parent_id) AS (
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

                   SELECT 13 , 'PRIVILEGE_3'         , CAST(NULL AS int8) UNION ALL
                   SELECT 14 , 'PRIVILEGE_3.1'       , 13                 UNION ALL
                   SELECT 15 , 'PRIVILEGE_3.2'       , 13                 UNION ALL
                   SELECT 16 , 'PRIVILEGE_3.2.1'     , 15                 UNION ALL
                   SELECT 17 , 'PRIVILEGE_3.2.2'     , 15                 UNION ALL

                   SELECT 19 , 'PRIVILEGE_4'         , CAST(NULL AS int8) UNION ALL
                   SELECT 20 , 'PRIVILEGE_4.1'       , 19                 UNION ALL
                   SELECT 21 , 'PRIVILEGE_4.2'       , 19                 UNION ALL
                   SELECT 22 , 'PRIVILEGE_4.2.1'     , 21                 UNION ALL
                   SELECT 23 , 'PRIVILEGE_4.2.2'     , 21
               ),
               names(creator, executor) AS (SELECT 'creator', 'executor'),

               roles(id, name) AS (
                   SELECT 1, (SELECT names.creator FROM names) UNION ALL
                   SELECT 2, (SELECT names.executor FROM names)
               ),

               creator(id, name) AS (SELECT roles.* FROM roles, names WHERE name = names.creator),
               executor(id, name) AS (SELECT roles.* FROM roles, names WHERE name = names.executor),



               privileges_1  (id, name) AS (SELECT * FROM privileges WHERE name = 'PRIVILEGES_1'),
               privileges_2  (id, name) AS (SELECT * FROM privileges WHERE name = 'PRIVILEGES_2'),
               privileges_3  (id, name) AS (SELECT * FROM privileges WHERE name = 'PRIVILEGES_3'),
               privileges_32 (id, name) AS (SELECT * FROM privileges WHERE name = 'PRIVILEGES_3.2'),
               privileges_322(id, name) AS (SELECT * FROM privileges WHERE name = 'PRIVILEGES_3.2.2'),

               roles_privileges(role_id, privilege_id) AS (
                   SELECT (SELECT id FROM creator),  (SELECT id FROM privileges_1)  UNION ALL
                   SELECT (SELECT id FROM creator),  (SELECT id FROM privileges_2)  UNION ALL
                   SELECT (SELECT id FROM creator),  (SELECT id FROM privileges_32) UNION ALL
                   SELECT (SELECT id FROM executor), (SELECT id FROM privileges_3)  UNION ALL
                   SELECT (SELECT id FROM executor), (SELECT id FROM privileges_322)
               )

SELECT name, id, parent_id as parent  FROM tree;


WITH RECURSIVE
    tree(name, id, parent_id) as (
        SELECT parent.name, parent.id, parent_id
        FROM privileges parent
                 LEFT JOIN roles_privileges rp on rp.privilege_id = parent.id
                 LEFT JOIN roles r on r.id = rp.role_id
        WHERE r.name in ('creator')
        UNION
        SELECT child.name, child.id, child.parent_id
        FROM privileges child
                 JOIN tree ON child.parent_id = tree.id
                 LEFT JOIN roles_privileges rp on rp.privilege_id = child.id
                 LEFT JOIN roles r on r.id = rp.role_id
    ),

    privileges(id, name, parent_id) AS (
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

        SELECT 13 , 'PRIVILEGE_3'         , CAST(NULL AS int8) UNION ALL
        SELECT 14 , 'PRIVILEGE_3.1'       , 13                 UNION ALL
        SELECT 15 , 'PRIVILEGE_3.2'       , 13                 UNION ALL
        SELECT 16 , 'PRIVILEGE_3.2.1'     , 15                 UNION ALL
        SELECT 17 , 'PRIVILEGE_3.2.2'     , 15                 UNION ALL

        SELECT 19 , 'PRIVILEGE_4'         , CAST(NULL AS int8) UNION ALL
        SELECT 20 , 'PRIVILEGE_4.1'       , 19                 UNION ALL
        SELECT 21 , 'PRIVILEGE_4.2'       , 19                 UNION ALL
        SELECT 22 , 'PRIVILEGE_4.2.1'     , 21                 UNION ALL
        SELECT 23 , 'PRIVILEGE_4.2.2'     , 21
    ),

    names(creator, executor, spectator) AS (SELECT 'creator', 'executor', 'spectator'),

    roles(id, name) AS (
        SELECT 1, (SELECT names.creator FROM names) UNION ALL
        SELECT 2, (SELECT names.executor FROM names) UNION ALL
        SELECT 3, (SELECT names.spectator FROM names)
    ),

    creator(id) AS (SELECT roles.id FROM roles, names WHERE name = names.creator),
    executor(id) AS (SELECT roles.id FROM roles, names WHERE name = names.executor),
    spectator(id) AS (SELECT roles.id FROM roles, names WHERE name = names.spectator),

    roles_privileges(role_id, privilege_id) AS (
        SELECT creator.id, privileges.id FROM creator, privileges WHERE name = 'PRIVILEGE_1' UNION ALL
        SELECT creator.id, privileges.id FROM creator, privileges WHERE name = 'PRIVILEGE_2' UNION ALL
        SELECT creator.id, privileges.id FROM creator, privileges WHERE name = 'PRIVILEGE_3.2' UNION ALL
        SELECT creator.id, privileges.id FROM creator, privileges WHERE name = 'PRIVILEGE_4.2.2' UNION ALL

        SELECT executor.id, privileges.id FROM executor, privileges WHERE name = 'PRIVILEGE_2.2' UNION ALL
        SELECT executor.id, privileges.id FROM executor, privileges WHERE name = 'PRIVILEGE_3.2.2' UNION ALL
        SELECT executor.id, privileges.id FROM executor, privileges WHERE name = 'PRIVILEGE_4.2.1' UNION ALL

        SELECT spectator.id, privileges.id FROM spectator, privileges WHERE name = 'PRIVILEGE_2.2'
    )
SELECT * FROM tree;













