--
-- DSL Bitraten GUI einbinden
--

INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ADD_SEPARATOR, ACTIVE)
  values (330,
  'de.augustakom.hurrican.gui.cps.actions.QueryDSLBitratesAction',
  'ACTION',  'query.dsl.bitrates', 'DSL Bitraten Ampel...', 'Ampel zeigt an, ob bei Tarifwechsel ein Portwechsel notwendig ist', null, 100, '1', '1');

INSERT INTO T_GUI_MAPPING (ID,  GUI_ID,  REFERENZ_ID, REFERENZ_HERKUNFT)
  values (S_T_GUI_MAPPING_0.nextVal, 330, 201, 'de.augustakom.hurrican.model.cc.gui.GUIDefinition');

