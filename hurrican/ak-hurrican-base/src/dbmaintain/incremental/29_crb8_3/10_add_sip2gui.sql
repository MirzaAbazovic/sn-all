INSERT INTO T_GUI_DEFINITION
(ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ADD_SEPARATOR, ACTIVE, VERSION) Values
(403, 'de.augustakom.hurrican.gui.auftrag.AuftragSIPPanel', 'PANEL',
 'auftrag.sip.panel', 'SIP-InterTrunk', 'SIP-InterTrunk ',
 '', 95, '', '1', 0);

INSERT INTO T_GUI_MAPPING
(ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT, VERSION) Values
(S_T_GUI_MAPPING_0.nextVal, 403, 530, 'de.augustakom.hurrican.model.cc.Produkt', 0);

