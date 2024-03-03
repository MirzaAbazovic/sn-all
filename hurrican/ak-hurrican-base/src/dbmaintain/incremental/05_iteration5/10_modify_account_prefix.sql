--
-- Script aktualisiert die Account-Prefixe
--

update T_PRODUKT set ACCOUNT_VORS='XA' where ACCOUNT_VORS='as';
update T_PRODUKT set ACCOUNT_VORS='XS' where PROD_ID in (312,313,32,33,34,35,300,301,314);
update T_PRODUKT set ACCOUNT_VORS='XS' where ACCOUNT_VORS='sd';
update T_PRODUKT set ACCOUNT_VORS='XS' where ACCOUNT_VORS='sk';

update T_TECH_LEISTUNG set PARAMETER='XF' where ID=7;
