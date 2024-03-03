-- Berechtigung fuer MenuItem 'cancellation.check' fuer Rolle 'auftrag.bearbeiter'
Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'cancellation.check',
   'de.augustakom.hurrican.gui.system.HurricanMainFrame', 'MenuItem', 1,  1);

insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='cancellation.check'
  and g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame';