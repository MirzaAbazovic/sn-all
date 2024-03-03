-- Panels Connect + Housing in GUI aufnehmen!

INSERT INTO T_PRODUKTGRUPPE (ID, PRODUKTGRUPPE, AM_RESPONSIBILITY) values (24, 'Housing', 1201);
update t_produkt set produktgruppe_id=24 where prod_id=370;

INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
   VALUES (218, 'de.augustakom.hurrican.gui.auftrag.AuftragConnectPanel', 'PANEL',
   'auftrag.connect.panel', 'Connect', 'Panel für spezielle Connect-Auftragsdaten', null, 30, 1);
INSERT INTO T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
    VALUES (S_T_GUI_MAPPING_0.nextVal, 218, 2, 'de.augustakom.hurrican.model.cc.ProduktGruppe');
INSERT INTO T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
    VALUES (S_T_GUI_MAPPING_0.nextVal, 218, 9, 'de.augustakom.hurrican.model.cc.ProduktGruppe');
INSERT INTO T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
    VALUES (S_T_GUI_MAPPING_0.nextVal, 218, 24, 'de.augustakom.hurrican.model.cc.ProduktGruppe');

INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
   VALUES (219, 'de.augustakom.hurrican.gui.auftrag.AuftragHousingPanel', 'PANEL',
   'auftrag.housing.panel', 'Housing', 'Panel für spezielle Housing-Auftragsdaten', null, 35, 1);
INSERT INTO T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
    VALUES (S_T_GUI_MAPPING_0.nextVal, 219, 2, 'de.augustakom.hurrican.model.cc.ProduktGruppe');
INSERT INTO T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
    VALUES (S_T_GUI_MAPPING_0.nextVal, 219, 9, 'de.augustakom.hurrican.model.cc.ProduktGruppe');
INSERT INTO T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
    VALUES (S_T_GUI_MAPPING_0.nextVal, 219, 24, 'de.augustakom.hurrican.model.cc.ProduktGruppe');
