--
-- T_PRODUKT um weiteres CPS Flag versehen.
--

alter table T_PRODUKT add CPS_AUTO_CREATION CHAR(1);
comment on column T_PRODUKT.CPS_AUTO_CREATION
  is 'Definiert, ob fuer das Produkt automatisch CPS-Tx erstellt werden duerfen.';

update T_PRODUKT set CPS_AUTO_CREATION='1' where CPS_PROD_NAME is not null;
update T_PRODUKT set CPS_AUTO_CREATION='0'
  where CPS_PROD_NAME like '%SDSL%';
update T_PRODUKT set CPS_AUTO_CREATION='1'
  where CPS_PROD_NAME like '%SDSL_S0';
