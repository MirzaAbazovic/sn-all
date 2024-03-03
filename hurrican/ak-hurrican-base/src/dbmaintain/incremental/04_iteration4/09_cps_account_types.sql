

alter table T_PRODUKT ADD CPS_ACCOUNT_TYPE VARCHAR2(20);
comment on column T_PRODUKT.CPS_ACCOUNT_TYPE IS 'Definiert den Radius Account-Typ fuer das Produkt';

update T_PRODUKT set CPS_ACCOUNT_TYPE='MAXI-HSI' where CPS_PROD_NAME is not null
  and (CPS_PROD_NAME LIKE '%DSL+%' or CPS_PROD_NAME LIKE 'AK-ADSL%'or CPS_PROD_NAME LIKE 'AKDSL%'
  or CPS_PROD_NAME LIKE 'MaxiPUR'or CPS_PROD_NAME LIKE 'MaxiKomplett%');
update T_PRODUKT set CPS_ACCOUNT_TYPE='SDSL-HSI' where CPS_PROD_NAME is not null
  and CPS_PROD_NAME LIKE '%SDSL%' and CPS_PROD_NAME not like '%SDSL_S0%';
update T_PRODUKT set CPS_ACCOUNT_TYPE='DIAL_IN' where CPS_PROD_NAME is not null
  and CPS_ACCOUNT_TYPE is null;
