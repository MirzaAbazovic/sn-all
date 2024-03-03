--
-- Produkte fuer Zusatzleitungen konfigurieren, damit sie die
-- Verbindungsbezeichnung des "Master"-Auftrags verwenden
--

alter table T_PRODUKT add TDN_USE_FROM_MASTER CHAR(1);
comment on column T_PRODUKT.TDN_USE_FROM_MASTER IS 'Flag definiert, dass die Verbindungsbezeichnung vom Master-Auftrag verwendet werden soll';

comment on column T_PRODUKT.TDN_KIND_OF_USE_PRODUCT IS 'Angabe des Prefix fuer <Nutzungsart - Produkt> der Verbindungsbezeichnung';
comment on column T_PRODUKT.TDN_KIND_OF_USE_TYPE IS 'Angabe des Prefix fuer <Nutzungsart - Typ> der Verbindungsbezeichnung';

update T_PRODUKT set TDN_KIND_OF_USE_PRODUCT=null, TDN_KIND_OF_USE_TYPE=null, TDN_USE_FROM_MASTER='1' where PROD_ID in (99,443,456);
