--
-- Berechtigung fuer IP Buttons
--

insert into ROLE (ID, NAME, APP_ID, IS_ADMIN, IS_PARENT) values (151, 'am.projekte', 1, '0', '0');

-- AM-Projekte MAs (Babic, Hieke, Niculae, Tschernich)
insert into USERROLE (ID, USER_ID, ROLE_ID) (select S_USERROLE_0.nextVal, 159, 151 from dual);
insert into USERROLE (ID, USER_ID, ROLE_ID) (select S_USERROLE_0.nextVal, 282, 151 from dual);
insert into USERROLE (ID, USER_ID, ROLE_ID) (select S_USERROLE_0.nextVal, 999, 151 from dual);
insert into USERROLE (ID, USER_ID, ROLE_ID) (select S_USERROLE_0.nextVal, 1379, 151 from dual);

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'new.lan.ip', 'de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel',
    'Button', 'Button, um eine neue IP-Adresse zu vergeben', 1);
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 151, '1', '1',
  g.ID from GUICOMPONENT g where g.name='new.lan.ip'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';

insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 151, '1', '1', 272 from dual;


