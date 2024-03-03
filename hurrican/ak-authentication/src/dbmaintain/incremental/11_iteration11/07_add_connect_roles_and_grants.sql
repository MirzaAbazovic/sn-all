
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 Values (S_GUICOMPONENT_0.nextval, 'save', 'de.augustakom.hurrican.gui.tools.physik.BreakRangierungDialog', 'BUTTON', 'Rangierung aufbrechen', 1, 0);


Insert into COMPBEHAVIOR (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextval, (select id from GUICOMPONENT where name = 'save' and parent = 'de.augustakom.hurrican.gui.tools.physik.BreakRangierungDialog'),
    (select id from ROLE where name = 'connect.user'), '1', '1', 0);

Insert into COMPBEHAVIOR (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextval, (select id from GUICOMPONENT where name = 'save' and parent = 'de.augustakom.hurrican.gui.shared.AssignEquipmentDialog'),
    (select id from ROLE where name = 'connect.user'), '1', '1', 0);

Insert into USERACCOUNT (ID, USER_ID, ACCOUNT_ID, DB_ID, VERSION)
 Values (S_USERACCOUNT_0.nextval, (select id from users where lower(loginname) = 'zpconnect'),
 (select id from account where acc_name = 'tal.default'), (select id from db where name = 'tal'), 0);
