-- VoIP-Leistungen erhalten VoIP-Command
Insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, VERSION)
 Values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 1009, 300, 'de.augustakom.hurrican.model.cc.TechLeistung', 0);
Insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, VERSION)
 Values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 1009, 301, 'de.augustakom.hurrican.model.cc.TechLeistung', 0);
Insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, VERSION)
 Values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 1009, 302, 'de.augustakom.hurrican.model.cc.TechLeistung', 0);

 
-- Billing-Produktleistungen zu DN-Leistungsbuendel konfigurieren
Insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, VERSION)
 Values (17, 71885, 3011, 0);
Insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, VERSION)
 Values (17, 71883, 3011, 0);
Insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, VERSION)
 Values (17, 72220, 3012, 0);
Insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, VERSION)
 Values (17, 72226, 3012, 0);
Insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, VERSION)
 Values (17, 72232, 3012, 0);

 
