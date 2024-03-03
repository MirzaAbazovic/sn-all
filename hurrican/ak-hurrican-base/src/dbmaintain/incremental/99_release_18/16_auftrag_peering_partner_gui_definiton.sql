INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE,
    NAME, TEXT,
    TOOLTIP,
    ORDER_NO, ACTIVE, VERSION)
 VALUES (406, 'de.augustakom.hurrican.gui.auftrag.AuftragPeeringPartnerPanel', 'PANEL',
    'auftrag.peeringpartner.panel', 'Peering Partner',
    'Panel zur Verwaltung der Peering Partner',
    25, '1', 0);

-- produktgruppe SDSL
INSERT INTO T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT, VERSION)
    VALUES (S_T_GUI_MAPPING_0.nextVal, 406, 4, 'de.augustakom.hurrican.model.cc.ProduktGruppe', 0);

-- Glasfaser SDSL
INSERT INTO T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT, VERSION)
    VALUES (S_T_GUI_MAPPING_0.nextVal, 406, 541, 'de.augustakom.hurrican.model.cc.Produkt', 0);

-- Glasfaser ADSL
INSERT INTO T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT, VERSION)
    VALUES (S_T_GUI_MAPPING_0.nextVal, 406, 542, 'de.augustakom.hurrican.model.cc.Produkt', 0);
