alter table T_PRODUKT add geoid_src VARCHAR2(8) default 'HVT' not null;
alter table T_PRODUKT add constraint CK_T_PRODUKT_GEOID_SRC check (geoid_src in ('HVT', 'GEO_ID', 'OHNE'));

update T_PRODUKT p set geoid_src = 'OHNE'
  where p.PROD_ID in (select p2p.prod_id from T_PRODUKT_2_PHYSIKTYP p2p where p2p.virtuell = '1')
;

update T_PRODUKT p set geoid_src = 'GEO_ID'
  where p.prod_id = 70
;
