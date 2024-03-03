Insert into T_LB_2_PRODUKT
   (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION, VERSION)
 (Select
   26, 87744, 3011, 'Prem. GF-DSL 150/15, IP-MGA', 0 from dual where not exists (select * from T_LB_2_PRODUKT where leistung__no=87744 and PRODUCT_OE__NO=3011));
;

Insert into T_LB_2_PRODUKT
   (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION, VERSION)
 (Select
   26, 87743, 3011, 'Prem. GF-DSL 150/15, IP-MGA Regio', 0 from dual where not exists (select * from T_LB_2_PRODUKT where leistung__no=87743 and PRODUCT_OE__NO=3011));
;

Insert into T_LB_2_PRODUKT
   (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION, VERSION)
  (Select
   27, 87742, 3011, 'Ü/Prem. GF-DSL Dfl 150/15 IP-TK', 0 from dual where not exists (select * from T_LB_2_PRODUKT where leistung__no=87742 and PRODUCT_OE__NO=3011));
;

Insert into T_LB_2_PRODUKT
   (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION, VERSION)
 (Select
   27, 87741, 3011, 'Ü/Prem. GF-DSL Dfl 150/15 IP-TK Regio', 0 from dual where not exists (select * from T_LB_2_PRODUKT where leistung__no=87741 and PRODUCT_OE__NO=3011));
