alter table T_PRODUKT add AUTO_HVT_ZUORDNUNG char(1) default '1' not null;
update T_PRODUKT set AUTO_HVT_ZUORDNUNG = '0' where PROD_ID = 580; -- SIP-Trunk
