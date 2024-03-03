-- Zuordnung Leistungsbuendel Surf&Fon
Insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION, VERSION)
 values (16, 72781, 3401, 'Surf+Fon Flat (18.000)', 0);
Insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION, VERSION)
 values (16, 72782, 3401, 'Surf+Fon Flat (50.000)', 0);
Insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION, VERSION)
 values (16, 72783, 3401, 'Surf+Fon Flat (100.000)', 0);
Insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION, VERSION)
 values (17, 72781, 72779, 3401, 'Surf+Fon Flat (18.000) Komfort', 0);
Insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION, VERSION)
 values (17, 72782, 72780, 3401, 'Surf+Fon Flat (50.000) Komfort', 0);


-- Zuordnung Leistungsbuendel TelefonFlat
Insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION, VERSION)
 values (16, 73013, 3403, 'Telefon Flat', 0);
 
 
-- Prod-Id 512: nur DSL, kein Phone-Anteil
update t_produkt_mapping set mapping_part_type='dsl' where mapping_group=1211; 
 
