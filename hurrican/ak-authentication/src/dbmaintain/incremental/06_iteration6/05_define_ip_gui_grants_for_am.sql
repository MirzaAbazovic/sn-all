--
-- Berechtigung fuer IP Buttons
--

insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='new.lan.ip'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';

insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='allocate.new.ip'
  and g.parent='de.augustakom.hurrican.gui.auftrag.internet.InternetPanel';


