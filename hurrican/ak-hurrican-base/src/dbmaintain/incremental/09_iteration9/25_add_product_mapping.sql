-- Diese Produktmappings bereiteten nach dem Release Probleme im Produktiv-System
-- -> wurden deshalb produktiv wieder entfernt.

-- Die Kombination aus EXT_PROD_NO und PROD_ID liefert unterschiedliche MAPPING_PART_TYPEs,
-- deshalb kommt es zu Probleme bei Ermittlung der "Produkt-Leistung" in Taifun in Funktion
-- de.augustakom.hurrican.service.billing.impl.LeistungServiceImpl.findProductLeistung4Auftrag(Integer, String)

INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE)
VALUES (602, 420, 421, 'phone');
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE)
VALUES (410, 410, 410, 'phone');
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE)
VALUES (603, 430, 430, 'phone');