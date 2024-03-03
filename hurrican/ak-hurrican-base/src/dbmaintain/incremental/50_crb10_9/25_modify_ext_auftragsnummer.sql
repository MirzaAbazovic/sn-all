
-- externeAuftragsnummer in IOArchive + MWFMeldung vergroessern (wg. "-kuedt" bzw. "-kuedt-1")
alter table T_IO_ARCHIVE modify WITA_EXT_ORDER_NO VARCHAR2(30);
alter table T_MWF_MELDUNG modify EXT_AUFTRAGS_NR VARCHAR2(30);
alter table T_USER_TASK modify EXTERNE_AUFTRAGSNUMMER VARCHAR2(30); 