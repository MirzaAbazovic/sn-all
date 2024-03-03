--
-- Drop-Commands um nicht mehr benoetigte Tabellen zu entfernen
--

ALTER TABLE t_leistung_snapshot drop foreign key FK_LEISTUNGSNAP_2_AUFTRAG;
DROP TABLE t_leistung_snapshot;
DROP TABLE t_verlauf_actions;

-- Aenderungen an T_AUFTRAG_DATEN
ALTER TABLE T_AUFTRAG_DATEN DROP COLUMN LA_GESP;

-- T_IP_ENDGERAET loeschen
ALTER TABLE T_IP_ENDGERAET drop foreign key FK_IPEG_2_AUFTRAG;
drop table T_IP_ENDGERAET;

-- T_ENDGERAET loeschen
ALTER TABLE T_ENDGERAET drop foreign key FK_T_ENDGERAET_1;
ALTER TABLE T_ENDGERAET drop foreign key FK_T_ENDGERAET_2;
ALTER TABLE T_ENDGERAET drop foreign key FK_T_ENDGERAET_3;
drop table T_ENDGERAET;

-- Spalte TYP von T_EG_CONFIG entfernen
ALTER TABLE T_EG_CONFIG drop column TYP;

-- Spalte EXT_PROD__NO von T_PRODUKT entfernen
ALTER TABLE T_PRODUKT drop column EXT_PRODUKT__NO;
