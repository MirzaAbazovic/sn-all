
-- Sonderkonfiguration fuer Alt-Produkte MUC (in Taifun nur DSL-Leistung geschrieben, keine Telephonie-Leistung!)
update T_PRODUKT_MAPPING set MAPPING_PART_TYPE='phone_dsl' where MAPPING_GROUP in (602,603,410) and MAPPING_PART_TYPE='phone';

-- DSL+ISDN (Maxi)
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE)
VALUES (604, 323, 420, 'phone');
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE)
VALUES (604, 421, 420, 'dsl');
