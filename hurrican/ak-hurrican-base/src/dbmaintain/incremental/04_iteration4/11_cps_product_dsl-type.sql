
update T_REFERENCE set INT_VALUE=3 where ID=14300;
update T_REFERENCE set INT_VALUE=7 where ID=14320;


alter table T_PRODUKT add CPS_DSL_PRODUCT CHAR(1);
comment on column T_PRODUKT.CPS_DSL_PRODUCT
  is 'Definiert, ob es sich fuer den CPS um ein xDSL Produkt handelt.';

update T_PRODUKT set CPS_DSL_PRODUCT='1'
  where CPS_PROD_NAME like '%DSL%' or CPS_PROD_NAME like '%Komplett%'
  or CPS_PROD_NAME like 'MaxiPUR';


