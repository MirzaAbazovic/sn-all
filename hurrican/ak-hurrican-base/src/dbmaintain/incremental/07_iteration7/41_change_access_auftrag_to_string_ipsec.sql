--
-- Als Uebergangsloesung alles moeglich da drin speichern
--

ALTER TABLE T_IPSEC_S2S
 ADD (ACCESS_AUFTRAG_NR  VARCHAR2(50 BYTE));

ALTER TABLE T_IPSEC_S2S DROP COLUMN ACCESS_AUFTRAG_ID;