-- Bemerkungen fuer Servicescheinmigration vergroessert
ALTER TABLE T_AUFTRAG_DATEN MODIFY(BEMERKUNGEN VARCHAR2(2000 BYTE));