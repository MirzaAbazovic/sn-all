update t_produkt set max_dn_count=1000, min_dn_count=1, dn_block='1', dn_typ=60 where prod_id=536;

Insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT, VERSION)
 values (S_T_GUI_MAPPING_0.nextVal, 202, 28, 'de.augustakom.hurrican.model.cc.ProduktGruppe', 0);

Insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, VERSION)
 values (25, 73457, 3422, 0);


 