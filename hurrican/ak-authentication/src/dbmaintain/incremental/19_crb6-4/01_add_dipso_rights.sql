INSERT INTO USERROLE (ID, USER_ID, ROLE_ID, VERSION)
    SELECT S_USERROLE_0.nextVal, sub.USER_ID, sub.ID, 0
    FROM
        (SELECT DISTINCT ur.USER_ID, r.ID
        FROM USERROLE ur, ROLE r
        WHERE LOWER(r.NAME) = 'verlauf.mqueue'
        AND ur.USER_ID NOT IN
            (SELECT DISTINCT ur.USER_ID
            FROM USERROLE ur
            WHERE ur.ROLE_ID =
                (SELECT r.ID
                FROM ROLE r
                WHERE LOWER(r.NAME) = 'verlauf.mqueue'))
        AND ur.USER_ID IN
            (SELECT u.ID
            FROM USERS u
            WHERE u.DEP_ID =
                (SELECT d.ID
                FROM DEPARTMENT d
                WHERE LOWER(d.Name) = 'dispo'))) sub;
