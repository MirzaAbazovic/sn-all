--
-- Anpassungen fuer Import der Online-Bestellung
--

alter table T_AUFTRAG_IMPORT add UPGRADE_18000 CHAR(1);
alter table T_AUFTRAG_IMPORT add ISDN_ANSCHLUSS CHAR(1);

commit;
