alter table T_PRODUKT add TDN_KIND_OF_USE_TYPE_VPN VARCHAR2(1);

update T_PRODUKT set TDN_KIND_OF_USE_TYPE_VPN='F' where PROD_ID in (462,454,453,452,451,450);
update T_PRODUKT set TDN_KIND_OF_USE_TYPE_VPN='C' where PROD_ID in (340,338,337,336,323,322,5,4,3,2,1);