--
-- Gui-Definition für "NBZ hinterlegen"
--
INSERT INTO t_gui_definition (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ADD_SEPARATOR, ACTIVE)
VALUES (327, 'de.augustakom.hurrican.gui.tools.nbz.actions.AttachNBZAction','ACTION', 'attach.nbz', 'NBZ hinterlegen', 'Hinterlegt NBZ für aktuellen Kunden', null, 100, 1, 1);

INSERT INTO t_gui_mapping (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
VALUES (S_T_GUI_MAPPING_0.nextVal, 327, 201, 'de.augustakom.hurrican.model.cc.gui.GUIDefinition');

INSERT INTO t_gui_mapping (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
VALUES (S_T_GUI_MAPPING_0.nextVal, 327, 100, 'de.augustakom.hurrican.model.cc.gui.GUIDefinition');