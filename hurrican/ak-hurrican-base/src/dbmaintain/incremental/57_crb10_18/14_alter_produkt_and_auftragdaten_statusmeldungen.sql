alter table T_PRODUKT drop column STATUSMELDUNGEN;
alter table T_AUFTRAG_DATEN add (STATUSMELDUNGEN char(1));