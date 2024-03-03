ALTER TABLE T_TECH_LEISTUNG ADD (AKTIV_BIS_NULL_ON_SYNC char(1));

-- fuer CPE soll aktivBis Datum nicht aus Taifun uebernommen werden
update T_TECH_LEISTUNG set AKTIV_BIS_NULL_ON_SYNC = 1 where id = 560;