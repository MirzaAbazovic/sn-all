--
-- Produkttabelle erweitern um Felder fuer Verbindungsbezeichnung

alter table T_PRODUKT add TDN_KIND_OF_USE_PRODUCT VARCHAR2(1);
alter table T_PRODUKT add TDN_KIND_OF_USE_TYPE VARCHAR2(1);


