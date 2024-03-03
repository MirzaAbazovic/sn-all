-- Surf&Fon 25.000 analog zu Surf&Fon 50.000 -> kein Doing noetig


-- Surf-Flat 25.000 und 50.000 - VDSL (Prio 1)
Insert into T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE, PRIORITY, VERSION)
 values (1212, 904, 512, 'phone_dsl', 1, 0);

-- Surf-Flat 25.000 und 50.000 - TAL (Prio 2) 'VDSL am HVT'
Insert into T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE, PRIORITY, VERSION)
 values (1213, 904, 515, 'phone_dsl', 2, 0);

 
-- Surf-Flat 100.000 - VDSL (Prio 1)
Insert into T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE, PRIORITY, VERSION)
 values (1214, 905, 512, 'phone_dsl', 1, 0);


-- Produkt Name -> EXT_PROD__NO, EXTERN_LEISTUNG__NO
-- Ü/Surf-Flat 25 -> 904, 10025
-- Ü/Surf-Flat 25 Regio -> 904, 10025
-- Ü/Surf-Flat 50 -> 904, 10026
-- Ü/Surf-Flat 50 Regio -> 904, 10026
-- Ü/Surf-Flat 100 -> 905, 10027
-- Ü/Surf&Fon-Flat 25 -> 901, 10025
-- Ü/Surf&Fon-Flat 25 Regio -> 901, 10025
