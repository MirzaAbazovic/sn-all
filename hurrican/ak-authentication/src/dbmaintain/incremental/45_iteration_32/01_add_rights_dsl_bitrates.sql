-- GUI Component definieren
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'query.dsl.bitrates', 'de.augustakom.hurrican.gui.system.HurricanMainFrame',
    'MenuItem', 'MenuItem, um die DSL Bitraten Ampel anzuzeigen', 1);

-- Component default_reader zuordnen
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 8, '1', '1',
  g.ID from GUICOMPONENT g where g.name='query.dsl.bitrates'
  and g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame';
