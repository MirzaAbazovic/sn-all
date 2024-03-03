
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 Values (S_GUICOMPONENT_0.nextval, 'edit.port.dtag', 'de.augustakom.hurrican.gui.auftrag.RangierungPanel', 'MenuItem', 'DTAG Port editieren', 1, 0);

Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 Values (S_GUICOMPONENT_0.nextval, 'save', 'de.augustakom.hurrican.gui.auftrag.EditDtagPortDialog', 'Button', 'DTAG Port editieren', 1, 0);


Insert into COMPBEHAVIOR (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextval, (select id from GUICOMPONENT where name = 'edit.port.dtag' and parent = 'de.augustakom.hurrican.gui.auftrag.RangierungPanel'),
    (select id from ROLE where name = 'connect.user'), '1', '1', 0);

Insert into COMPBEHAVIOR (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextval, (select id from GUICOMPONENT where name = 'save' and parent = 'de.augustakom.hurrican.gui.auftrag.EditDtagPortDialog'),
    (select id from ROLE where name = 'connect.user'), '1', '1', 0);

Insert into COMPBEHAVIOR (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextval, (select id from GUICOMPONENT where name = 'save' and parent = 'de.augustakom.hurrican.gui.base.tree.hardware.PdhLeisteDialog'),
    (select id from ROLE where name = 'connect.user'), '1', '1', 0);


Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 Values (S_GUICOMPONENT_0.nextval, 'edit.baugruppe', 'de.augustakom.hurrican.gui.hvt.HWBaugruppenAdminPanel', 'MenuItem', 'Baugruppe editieren', 1, 0);

Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 Values (S_GUICOMPONENT_0.nextval, 'save', 'de.augustakom.hurrican.gui.hvt.HWBaugruppenEditDialog', 'Button', 'Baugruppe speichern', 1, 0);


Insert into COMPBEHAVIOR (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextval, (select id from GUICOMPONENT where name = 'edit.baugruppe' and parent = 'de.augustakom.hurrican.gui.hvt.HWBaugruppenAdminPanel'),
    (select id from ROLE where name = 'admin.hvt'), '1', '1', 0);

Insert into COMPBEHAVIOR (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextval, (select id from GUICOMPONENT where name = 'generate.ports' and parent = 'de.augustakom.hurrican.gui.hvt.HWBaugruppenAdminPanel'),
    (select id from ROLE where name = 'admin.hvt'), '1', '1', 0);

Insert into COMPBEHAVIOR (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextval, (select id from GUICOMPONENT where name = 'save' and parent = 'de.augustakom.hurrican.gui.hvt.HWBaugruppenEditDialog'),
    (select id from ROLE where name = 'admin.hvt'), '1', '1', 0);

Insert into COMPBEHAVIOR (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextval, (select id from GUICOMPONENT where name = 'save' and parent = 'de.augustakom.hurrican.gui.hvt.PortGenerationDialog'),
    (select id from ROLE where name = 'admin.hvt'), '1', '1', 0);


Update ROLE set NAME = 'connect.admin' where NAME = 'connect.user';


Insert into USERROLE (ID, USER_ID, ROLE_ID, VERSION)
 Values (S_USERROLE_0.nextval, (select id from users where lower(loginname) = 'zpconnect'), (select id from role where name = 'physik.master'), 0);

Insert into USERROLE (ID, USER_ID, ROLE_ID, VERSION)
 Values (S_USERROLE_0.nextval, (select id from users where lower(loginname) = 'walinski'), (select id from role where name = 'physik.master'), 0);

Insert into USERROLE (ID, USER_ID, ROLE_ID, VERSION)
 Values (S_USERROLE_0.nextval, (select id from users where lower(loginname) = 'pacher'), (select id from role where name = 'physik.master'), 0);
