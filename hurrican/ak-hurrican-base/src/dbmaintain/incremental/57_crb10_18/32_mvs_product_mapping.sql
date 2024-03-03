-- MVS Enterprise Produkt - Abhaengigkeit nicht mehr 1-zu-1, da Ueberlassungsleistung mehrfach gebucht werden kann (fuer Anzahl Nebenstellen)
update t_produkt set AKTIONS_ID=2 where PROD_ID=535;

-- Produktmapping fuer MVS Enterprise
Insert into T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE, PRIORITY, VERSION)
 values (535, 535, 535, 'phone', 1, 0);
-- Produktmapping fuer MVS Site
Insert into T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE, PRIORITY, VERSION)
 values (536, 536, 536, 'phone', 1, 0);
