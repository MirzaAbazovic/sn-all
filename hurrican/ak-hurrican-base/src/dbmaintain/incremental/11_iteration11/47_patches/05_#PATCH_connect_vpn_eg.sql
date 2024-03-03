insert into T_EG (ID, NAME, BESCHREIBUNG, VERFUEGBAR_VON, VERFUEGBAR_BIS, CONFIGURABLE, TYPE)
  values (S_T_EG_0.nextVal, 'FV-Router', 'Festverbindungs-Router für Connect/VPN', to_date('01.01.2010', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), '1', 4);
update T_EG set INTERNE__ID=ID where INTERNE__ID is null;
  
insert into T_PROD_2_EG (ID, PROD_ID, IS_ACTIVE, EG_ID) select S_T_PROD_2_EG_0.nextVal, 450, '1', eg.ID from T_EG eg where eg.NAME='FV-Router';
insert into T_PROD_2_EG (ID, PROD_ID, IS_ACTIVE, EG_ID) select S_T_PROD_2_EG_0.nextVal, 451, '1', eg.ID from T_EG eg where eg.NAME='FV-Router';
insert into T_PROD_2_EG (ID, PROD_ID, IS_ACTIVE, EG_ID) select S_T_PROD_2_EG_0.nextVal, 452, '1', eg.ID from T_EG eg where eg.NAME='FV-Router';
insert into T_PROD_2_EG (ID, PROD_ID, IS_ACTIVE, EG_ID) select S_T_PROD_2_EG_0.nextVal, 453, '1', eg.ID from T_EG eg where eg.NAME='FV-Router';
insert into T_PROD_2_EG (ID, PROD_ID, IS_ACTIVE, EG_ID) select S_T_PROD_2_EG_0.nextVal, 454, '1', eg.ID from T_EG eg where eg.NAME='FV-Router';
insert into T_PROD_2_EG (ID, PROD_ID, IS_ACTIVE, EG_ID) select S_T_PROD_2_EG_0.nextVal, 455, '1', eg.ID from T_EG eg where eg.NAME='FV-Router';
