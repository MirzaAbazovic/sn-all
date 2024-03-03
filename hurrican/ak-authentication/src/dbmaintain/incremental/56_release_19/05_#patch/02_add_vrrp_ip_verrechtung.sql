INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
SELECT S_GUICOMPONENT_0.nextVal, 'new.lan.vrrp.ip', 'de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel',
   'Button', 'Legt eine neue LAN-VRRP-IP für das Endgerät an.', 1, 0 from dual
where not exists (select * from GUICOMPONENT where name = 'new.lan.vrrp.ip' AND type = 'Button');

INSERT INTO COMPBEHAVIOR (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
SELECT S_COMPBEHAVIOR_0.nextVal,
   (SELECT id FROM GUICOMPONENT WHERE name = 'new.lan.vrrp.ip' AND type = 'Button'),
   (SELECT id FROM ROLE WHERE name = 'am.projekte'), '1', '1', 0 from dual
where not exists (select * from COMPBEHAVIOR where
                        COMP_ID = (SELECT id FROM GUICOMPONENT WHERE name = 'new.lan.vrrp.ip' AND type = 'Button')
                        and ROLE_ID = (SELECT id FROM ROLE WHERE name = 'am.projekte'));

INSERT INTO COMPBEHAVIOR (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
SELECT S_COMPBEHAVIOR_0.nextVal,
   (SELECT id FROM GUICOMPONENT WHERE name = 'new.lan.vrrp.ip' AND type = 'Button'),
   (SELECT id FROM ROLE WHERE name = 'auftrag.bearbeiter'), '1', '1', 0 from dual
where not exists (select * from COMPBEHAVIOR where
                        COMP_ID = (SELECT id FROM GUICOMPONENT WHERE name = 'new.lan.vrrp.ip' AND type = 'Button')
                        and ROLE_ID = (SELECT id FROM ROLE WHERE name = 'auftrag.bearbeiter'));


INSERT INTO COMPBEHAVIOR (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
SELECT S_COMPBEHAVIOR_0.nextVal,
   (SELECT id FROM GUICOMPONENT WHERE name = 'new.lan.vrrp.ip' AND type = 'Button'),
   (SELECT id FROM ROLE WHERE name = 'verlauf.stonline'), '1', '1', 0 from dual
where not exists (select * from COMPBEHAVIOR where
                        COMP_ID = (SELECT id FROM GUICOMPONENT WHERE name = 'new.lan.vrrp.ip' AND type = 'Button')
                        and ROLE_ID = (SELECT id FROM ROLE WHERE name = 'verlauf.stonline'));

