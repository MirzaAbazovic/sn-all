INSERT INTO USERROLE (ID, USER_ID, ROLE_ID)
VALUES (
    S_USERROLE_0.nextVal, 
    (SELECT ID FROM USERS WHERE LOGINNAME = 'SymaderSt'), 
    (SELECT ID FROM ROLE where NAME = 'olt.migration')
);

INSERT INTO USERROLE (ID, USER_ID, ROLE_ID)
VALUES (
    S_USERROLE_0.nextVal, 
    (SELECT ID FROM USERS WHERE LOGINNAME = 'TrikasSt'), 
    (SELECT ID FROM ROLE where NAME = 'olt.migration')
);

INSERT INTO USERROLE (ID, USER_ID, ROLE_ID)
VALUES (
    S_USERROLE_0.nextVal, 
    (SELECT ID FROM USERS WHERE LOGINNAME = 'TuerkogluOem'), 
    (SELECT ID FROM ROLE where NAME = 'olt.migration')
);

INSERT INTO USERROLE (ID, USER_ID, ROLE_ID)
VALUES (
    S_USERROLE_0.nextVal, 
    (SELECT ID FROM USERS WHERE LOGINNAME = 'PotratzRa'), 
    (SELECT ID FROM ROLE where NAME = 'olt.migration')
);

INSERT INTO USERROLE (ID, USER_ID, ROLE_ID)
VALUES (
    S_USERROLE_0.nextVal, 
    (SELECT ID FROM USERS WHERE LOGINNAME = 'ZimmermannGr'), 
    (SELECT ID FROM ROLE where NAME = 'olt.migration')
);