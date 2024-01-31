 WITH RECURSIVE
    tree(privilege_parent_id, privilege_id, privilege) as (
        SELECT parent_id, parent.id as id, parent.name
        FROM privileges parent
                 LEFT JOIN roles_privileges rp ON parent.id = rp.privilege_id
                 LEFT JOIN roles r ON r.id = rp.role_id
        WHERE parent_id IS NULL
          AND r.name IN ('creator')
        UNION ALL
        SELECT child.parent_id, child.id as id, child.name
        FROM privileges child
                 JOIN tree ON child.parent_id = tree.privilege_id
    ),

    names(creator, executor) AS (SELECT 'creator', 'executor'),

    roles(id, name) AS (
        SELECT 1, (SELECT names.creator FROM names) UNION ALL
        SELECT 2, (SELECT names.executor FROM names)
    ),

    creator (id, name) AS (SELECT roles.* FROM roles, names WHERE name = names.creator),
    executor(id, name) AS (SELECT roles.* FROM roles, names WHERE name = names.executor),

    privileges(id, name, parent_id) AS (
        SELECT 1 , 'PRIVILEGES_1'     , CAST(NULL AS int2) UNION ALL
        SELECT 2 , 'PRIVILEGES_2'     , CAST(NULL AS int2) UNION ALL
        SELECT 3 , 'PRIVILEGES_3'     , CAST(NULL AS int2) UNION ALL
        SELECT 4 , 'PRIVILEGES_2.1'   , 2                  UNION ALL
        SELECT 5 , 'PRIVILEGES_2.2'   , 2                  UNION ALL
        SELECT 6 , 'PRIVILEGES_3.1'   , 3                  UNION ALL
        SELECT 7 , 'PRIVILEGES_3.2'   , 3                  UNION ALL
        SELECT 8 , 'PRIVILEGES_2.1.1' , 4                  UNION ALL
        SELECT 8 , 'PRIVILEGES_2.1.2' , 4
    ),

    privileges_1(id, name) AS (SELECT * FROM privileges WHERE name = 'PRIVILEGES_1'),
    privileges_2(id, name) AS (SELECT * FROM privileges WHERE name = 'PRIVILEGES_2'),
    privileges_3(id, name) AS (SELECT * FROM privileges WHERE name = 'PRIVILEGES_3'),

    roles_privileges(role_id, privilege_id) AS (
        SELECT (SELECT id FROM creator),  (SELECT id FROM privileges_1) UNION ALL
        SELECT (SELECT id FROM creator),  (SELECT id FROM privileges_2) UNION ALL
        SELECT (SELECT id FROM executor), (SELECT id FROM privileges_3)
    )
SELECT * FROM tree;


 WITH RECURSIVE
     tree(parent_id, id, privilege) as (
         SELECT parent_id, p.id as id, p.name
         FROM root, privileges p
                  LEFT JOIN roles_privileges rp ON p.id = rp.privilege_id
                  LEFT JOIN roles r ON r.id = rp.role_id
         WHERE p.id = root.id
         UNION ALL
         SELECT child.parent_id, child.id as id, child.name
         FROM privileges child
                  JOIN tree ON child.parent_id = tree.id
     ),
     root(id) as (SELECT 2),
     names(creator, executor) AS (SELECT 'creator', 'executor'),

     roles(id, name) AS (
         SELECT 1, (SELECT names.creator FROM names) UNION ALL
         SELECT 2, (SELECT names.executor FROM names)
     ),

     creator (id, name) AS (SELECT roles.* FROM roles, names WHERE name = names.creator),
     executor(id, name) AS (SELECT roles.* FROM roles, names WHERE name = names.executor),

     privileges(id, name, parent_id) AS (
         SELECT 1 , 'PRIVILEGES_1'     , CAST(NULL AS int2) UNION ALL
         SELECT 2 , 'PRIVILEGES_2'     , CAST(NULL AS int2) UNION ALL
         SELECT 3 , 'PRIVILEGES_3'     , CAST(NULL AS int2) UNION ALL
         SELECT 4 , 'PRIVILEGES_2.1'   , 2                  UNION ALL
         SELECT 5 , 'PRIVILEGES_2.2'   , 2                  UNION ALL
         SELECT 6 , 'PRIVILEGES_3.1'   , 3                  UNION ALL
         SELECT 7 , 'PRIVILEGES_3.2'   , 3                  UNION ALL
         SELECT 8 , 'PRIVILEGES_2.1.1' , 4                  UNION ALL
         SELECT 8 , 'PRIVILEGES_2.1.2' , 4
     ),

     privileges_1(id, name) AS (SELECT * FROM privileges WHERE name = 'PRIVILEGES_1'),
     privileges_2(id, name) AS (SELECT * FROM privileges WHERE name = 'PRIVILEGES_2'),
     privileges_3(id, name) AS (SELECT * FROM privileges WHERE name = 'PRIVILEGES_3'),

     roles_privileges(role_id, privilege_id) AS (
         SELECT (SELECT id FROM creator),  (SELECT id FROM privileges_1) UNION ALL
         SELECT (SELECT id FROM creator),  (SELECT id FROM privileges_2) UNION ALL
         SELECT (SELECT id FROM executor), (SELECT id FROM privileges_3)
     )

SELECT privilege, id, parent_id as parent  FROM tree;













