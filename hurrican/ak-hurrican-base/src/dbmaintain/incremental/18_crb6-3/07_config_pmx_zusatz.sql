
update T_PRODUKT set AUFTRAGSERSTELLUNG='0' where PROD_ID=443;
update T_PRODUKT set CREATE_AP_ADDRESS='0' where PROD_ID=443;
update T_PRODUKT set AKTIONS_ID=1 where PROD_ID=443;

update T_PRODUKT set TDN_KIND_OF_USE_PRODUCT=NULL, TDN_KIND_OF_USE_TYPE=NULL, TDN_USE_FROM_MASTER='1' where PROD_ID=443;
