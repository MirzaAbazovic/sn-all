--
-- CPS History GUI einbinden
--

INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ADD_SEPARATOR, ACTIVE)
  values (322,
  'de.augustakom.hurrican.gui.cps.actions.ShowCPSHistoryAction',
  'ACTION',  'cps.history.anzeigen', 'CPS-Historie...', 'Zeigt die CPS-Historie des Auftrags an', null, 36, null, '1');

INSERT INTO T_GUI_MAPPING (ID,  GUI_ID,  REFERENZ_ID, REFERENZ_HERKUNFT)
  values (S_T_GUI_MAPPING_0.nextVal, 322, 201, 'de.augustakom.hurrican.model.cc.gui.GUIDefinition');

