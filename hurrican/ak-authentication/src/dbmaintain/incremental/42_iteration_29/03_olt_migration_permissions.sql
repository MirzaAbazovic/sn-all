INSERT INTO role (ID, NAME, DESCRIPTION, APP_ID, IS_ADMIN) 
VALUES (S_ROLE_0.nextVal, 'olt.migration', 'Rollenrechte: <br>\n - Datum für OLT-Migration auf Mandantenfähigkeit setzen', 1, 0);

INSERT INTO guicomponent (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID) 
VALUES (S_GUICOMPONENT_0.nextVal, 'migriert', 'de.augustakom.hurrican.gui.hvt.HWOltAdminPanel', 'DateComponent', 'Datum für OLT-Migration auf Mandantenfähigkeit', 1);

INSERT INTO compbehavior (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE) 
VALUES (S_COMPBEHAVIOR_0.nextVal,
    (select id from guicomponent where name = 'migriert'), 
    (select id from role where name = 'olt.migration'),     
    1, 1);
    
INSERT INTO USERROLE (ID, USER_ID, ROLE_ID)
VALUES (
    S_USERROLE_0.nextVal, 
    (SELECT ID FROM USERS WHERE LOGINNAME = 'EcksteinBe'), 
    (SELECT ID FROM ROLE where NAME = 'olt.migration')
);

INSERT INTO USERROLE (ID, USER_ID, ROLE_ID)
VALUES (
    S_USERROLE_0.nextVal, 
    (SELECT ID FROM USERS WHERE LOGINNAME = 'EcksteinBe'), 
    (SELECT ID FROM ROLE where NAME = 'admin.hvt')
);

INSERT INTO USERROLE (ID, USER_ID, ROLE_ID)
VALUES (
    S_USERROLE_0.nextVal, 
    (SELECT ID FROM USERS WHERE LOGINNAME = 'zeilmeieran'), 
    (SELECT ID FROM ROLE where NAME = 'olt.migration')
);
