Insert into T_LB_2_PRODUKT
   (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION, VERSION)
 (Select 16, 87213, 3401, 'Surf+Fon-Flat 150 (IPv6)', 0 from dual where not exists (select * from T_LB_2_PRODUKT where LEISTUNG__NO = 87213 and PRODUCT_OE__NO = 3401));
