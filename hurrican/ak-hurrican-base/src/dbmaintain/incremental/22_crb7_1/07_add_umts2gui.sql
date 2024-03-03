INSERT INTO T_GUI_DEFINITION
(ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ADD_SEPARATOR, ACTIVE, VERSION) Values
(402, 'de.augustakom.hurrican.gui.auftrag.AuftragUMTSPanel', 'PANEL',
 'auftrag.umts.panel', 'IP-VPN UMTS', 'IP-VPN UMTS Zugangsvariante und Backup für xDSL IP-VPN Anschlüsse',
 '', 90, '', '1', 0);

 INSERT INTO T_GUI_MAPPING
(ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT, VERSION) Values
(S_T_GUI_MAPPING_0.nextVal, 402, 470, 'de.augustakom.hurrican.model.cc.Produkt', 0);

INSERT INTO T_GUI_MAPPING
(ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT, VERSION) Values
(S_T_GUI_MAPPING_0.nextVal, 402, 471, 'de.augustakom.hurrican.model.cc.Produkt', 0);
