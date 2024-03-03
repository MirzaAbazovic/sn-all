-- SDSL-Router (mit Konfiguration) den SDSL-Produkten zuordnen

delete from T_PROD_2_EG where PROD_ID=460;

insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT, IS_ACTIVE) values (S_T_PROD_2_EG_0.nextVal, 460, 12, '0', '1');
