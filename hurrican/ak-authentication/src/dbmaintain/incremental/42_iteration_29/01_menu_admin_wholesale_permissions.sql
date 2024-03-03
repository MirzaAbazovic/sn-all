INSERT INTO role (ID, NAME, DESCRIPTION, APP_ID, IS_ADMIN) 
VALUES (S_ROLE_0.nextVal, 'admin.wholesale', 'Rollenrechte: <br>\n - EKPs und A10NSPs bearbeiten', 1, 0);

INSERT INTO guicomponent (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID) 
VALUES (S_GUICOMPONENT_0.nextVal, 'admin.wholesale.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame', 'MenuItem', 'MenuItem für die Administration \nder EKPs und A10NSPs', 1);

INSERT INTO compbehavior (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE) 
VALUES (S_COMPBEHAVIOR_0.nextVal,
    (select id from guicomponent where name = 'admin.wholesale.action'), 
    (select id from role where name = 'admin.wholesale'),     
    1, 1);
