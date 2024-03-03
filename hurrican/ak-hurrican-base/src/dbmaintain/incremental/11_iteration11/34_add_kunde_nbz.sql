--
-- Tabelle fuer Zuordnung Kunde NBZ
--
CREATE TABLE T_KUNDE_NBZ
(
  ID            INTEGER NOT NULL,
  KUNDE__NO     INTEGER NOT NULL,
  NBZ           VARCHAR(50) NOT NULL,
  VERSION       NUMBER(18) DEFAULT 0 NOT NULL,
  CONSTRAINT KUNDE__NO_unique UNIQUE (KUNDE__NO)
);

CREATE SEQUENCE S_T_KUNDE_NBZ
    MINVALUE 1
    START WITH 1
    INCREMENT BY 1
    CACHE 20;


--
-- View fuer Migration von vorhandenen NBZ
--
-- (alle TDN, die mit 3 Buchstaben, 3 Ziffern und "." beginnen,
-- Bsp. lhm051.001.001)
--
CREATE OR REPLACE VIEW V_MIG_KUNDENNR AS
SELECT  T_AUFTRAG_TECHNIK.auftrag_id,
        T_TDN.tdn,
        T_AUFTRAG.kunde__no
FROM    T_TDN,
        T_AUFTRAG_TECHNIK,
        T_AUFTRAG
WHERE   T_TDN.id = T_AUFTRAG_TECHNIk.tdn_id
AND     T_AUFTRAG.id = T_AUFTRAG_TECHNIK.auftrag_id
AND     T_AUFTRAG_TECHNIK.VPN_ID IS NULL
AND     REGEXP_LIKE (T_TDN.TDN, '^[a-zA-Z]{3}[0-9]{3}\.+')
ORDER BY T_AUFTRAG.kunde__no ASC;