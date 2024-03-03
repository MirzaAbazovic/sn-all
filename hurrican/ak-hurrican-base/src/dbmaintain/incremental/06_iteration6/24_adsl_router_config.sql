-- ADSL-Router (mit Konfiguration) den ADSL-Produkten zuordnen

insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT, IS_ACTIVE) values (S_T_PROD_2_EG_0.nextVal, 460, 50, '0', '1');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT, IS_ACTIVE) values (S_T_PROD_2_EG_0.nextVal, 430, 50, '0', '1');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT, IS_ACTIVE) values (S_T_PROD_2_EG_0.nextVal, 431, 50, '0', '1');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT, IS_ACTIVE) values (S_T_PROD_2_EG_0.nextVal, 420, 50, '0', '1');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT, IS_ACTIVE) values (S_T_PROD_2_EG_0.nextVal, 421, 50, '0', '1');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT, IS_ACTIVE) values (S_T_PROD_2_EG_0.nextVal, 410, 50, '0', '1');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT, IS_ACTIVE) values (S_T_PROD_2_EG_0.nextVal, 411, 50, '0', '1');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT, IS_ACTIVE) values (S_T_PROD_2_EG_0.nextVal, 400, 50, '0', '1');

update T_PROD_2_EG set IS_ACTIVE='1' where PROD_ID=440 and EG_ID=50;
