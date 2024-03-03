INSERT INTO USERROLE (ID, USER_ID, ROLE_ID, VERSION)
    SELECT S_USERROLE_0.NEXTVAL, u.ID, r.ID, 0
    FROM USERS u, ROLE r
    WHERE U.LOGINNAME in ('WendtSi', 'Garbe', 'DhamiGu', 'WeissMi')
        AND r.NAME = 'auftrag.bearbeiter.leitungsnummer';
