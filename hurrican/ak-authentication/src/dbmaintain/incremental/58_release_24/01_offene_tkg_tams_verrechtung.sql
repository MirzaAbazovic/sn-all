INSERT into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  (Select S_GUICOMPONENT_0.nextVal, 'task.freigeben', 'de.augustakom.hurrican.gui.tools.tal.OffeneTKGTamsPanel', 'Button', 1, 0 from dual
  where not exists (select 1 from GUICOMPONENT where name = 'task.freigeben' and parent = 'de.augustakom.hurrican.gui.tools.tal.OffeneTKGTamsPanel'));

INSERT INTO COMPBEHAVIOR (ID, VISIBLE, EXECUTABLE, COMP_ID, ROLE_ID)
    (SELECT  S_COMPBEHAVIOR_0.nextVal, '1', '1', COMP_ID_TMP, ROLE_ID_TMP
      FROM
        (select ID as COMP_ID_TMP from  GUICOMPONENT WHERE name = 'task.freigeben' AND parent = 'de.augustakom.hurrican.gui.tools.tal.OffeneTKGTamsPanel'),
        (select ID as ROLE_ID_TMP FROM  ROLE WHERE name='wita.superuser')
    where not exists (select 1 from GUICOMPONENT where name = 'task.freigeben' and parent = 'de.augustakom.hurrican.gui.tools.tal.OffeneTKGTamsPanel'));



INSERT into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  (select S_GUICOMPONENT_0.nextVal, 'task.bearbeiten', 'de.augustakom.hurrican.gui.tools.tal.OffeneTKGTamsPanel', 'Button', 1, 0 from dual
  where not exists (select 1 from GUICOMPONENT where name = 'task.bearbeiten' and parent = 'de.augustakom.hurrican.gui.tools.tal.OffeneTKGTamsPanel'));

INSERT INTO COMPBEHAVIOR (ID, VISIBLE, EXECUTABLE, COMP_ID, ROLE_ID)
    (SELECT  S_COMPBEHAVIOR_0.nextVal, '1', '1', COMP_ID_TMP, ROLE_ID_TMP
      FROM
        (select ID as COMP_ID_TMP from GUICOMPONENT WHERE name = 'task.bearbeiten' AND parent = 'de.augustakom.hurrican.gui.tools.tal.OffeneTKGTamsPanel'),
        (select ID as ROLE_ID_TMP FROM ROLE WHERE name='wita.superuser')
where not exists (select 1 from GUICOMPONENT where name = 'task.bearbeiten' and parent = 'de.augustakom.hurrican.gui.tools.tal.OffeneTKGTamsPanel'));