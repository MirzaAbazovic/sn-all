
update GUICOMPONENT set name = 'create.pdhleiste' where name = 'pdhleiste.create' and parent = 'de.augustakom.hurrican.gui.base.tree.hardware.HardwareTreeFrame';

Insert into ROLE (ID, NAME, DESCRIPTION, APP_ID, IS_ADMIN, IS_PARENT, VERSION)
 Values (S_ROLE_0.nextval, 'connect.user', 'Rolle fuer Connect-Bearbeiter', 1, '0', NULL, 0);


Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 Values (S_GUICOMPONENT_0.nextval, 'break.rangierung', 'de.augustakom.hurrican.gui.hvt.SearchRangierungPanel', 'BUTTON', 'Button, um eine 
Rangierung aufzubrechen', 1, 0);

Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 Values (S_GUICOMPONENT_0.nextval, 'break.rangierung', 'de.augustakom.hurrican.gui.auftrag.RangierungPanel', 'BUTTON', 'Button, um eine 
Rangierung aufzubrechen', 1, 0);

Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 Values (S_GUICOMPONENT_0.nextval, 'create.rangierung', 'de.augustakom.hurrican.gui.auftrag.RangierungPanel', 'BUTTON', 'Button, um eine 
Rangierung manuell aufzubauen', 1, 0);


Insert into COMPBEHAVIOR (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextval, (select id from GUICOMPONENT where name = 'break.rangierung' and parent = 'de.augustakom.hurrican.gui.hvt.SearchRangierungPanel'),
    (select id from ROLE where name = 'connect.user'), '1', '1', 0);

Insert into COMPBEHAVIOR (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextval, (select id from GUICOMPONENT where name = 'break.rangierung' and parent = 'de.augustakom.hurrican.gui.auftrag.RangierungPanel'),
    (select id from ROLE where name = 'connect.user'), '1', '1', 0);

Insert into COMPBEHAVIOR (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextval, (select id from GUICOMPONENT where name = 'create.rangierung' and parent = 'de.augustakom.hurrican.gui.auftrag.RangierungPanel'),
    (select id from ROLE where name = 'connect.user'), '1', '1', 0);

Insert into COMPBEHAVIOR (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextval, (select id from GUICOMPONENT where name = 'create.pdhleiste' and parent = 'de.augustakom.hurrican.gui.base.tree.hardware.HardwareTreeFrame'),
    (select id from ROLE where name = 'connect.user'), '1', '1', 0);

Insert into COMPBEHAVIOR (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextval, (select id from GUICOMPONENT where name = 'its.tools' and parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame'),
    (select id from ROLE where name = 'connect.user'), '1', '1', 0);

Insert into COMPBEHAVIOR (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextval, (select id from GUICOMPONENT where name = 'its.remove.rangierung.action' and parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame'),
    (select id from ROLE where name = 'connect.user'), '1', '1', 0);


Insert into USERROLE (ID, USER_ID, ROLE_ID, VERSION)
 Values (S_USERROLE_0.nextval, (select id from users where lower(loginname) = 'zpconnect'), (select id from role where name = 'connect.user'), 0);

Insert into USERROLE (ID, USER_ID, ROLE_ID, VERSION)
 Values (S_USERROLE_0.nextval, (select id from users where lower(loginname) = 'walinski'), (select id from role where name = 'connect.user'), 0);

Insert into USERROLE (ID, USER_ID, ROLE_ID, VERSION)
 Values (S_USERROLE_0.nextval, (select id from users where lower(loginname) = 'pacher'), (select id from role where name = 'connect.user'), 0);
