--
-- SQL-Script, das die Tabellen fuer die IPs auf den Endgeraeten
-- erzeugt und damit eine 1:n Beziehung zulaesst.
--

CREATE TABLE T_EG_IP (
  ID                NUMBER(10)                  NOT NULL,
  EG2A_ID           NUMBER(10)                  NOT NULL,
  IP_ADDRESS        VARCHAR2(40 BYTE)           NOT NULL,
  SUBNET_MASK       VARCHAR2(40 BYTE),
  TYP               VARCHAR2(10 BYTE)           NOT NULL
);
COMMENT ON TABLE T_EG_IP IS 'Tabelle um die IPs fuer die Endgeraetekonfiguration zu halten';
COMMENT ON COLUMN T_EG_IP.EG2A_ID IS 'ID des Eintrags in T_EG_2_AUFTRAG';
COMMENT ON COLUMN T_EG_IP.IP_ADDRESS IS 'Die IP-Adresse';
COMMENT ON COLUMN T_EG_IP.SUBNET_MASK IS 'Die Subnetz-Maske';
COMMENT ON COLUMN T_EG_IP.TYP IS 'Was ist der Typ der IP-Adresse? WAN/LAN/...';
commit;

ALTER TABLE T_EG_IP ADD CONSTRAINT PK_T_EG_IP PRIMARY KEY (ID);
commit;

-- ForeignKey von T_EG_IP.EG2A_ID auf T_EG_2_AUFTRAG
CREATE INDEX IX_FK_EG_IP_2_EG_2_AUFTRAG ON T_EG_IP (EG2A_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_EG_IP
  ADD CONSTRAINT FK_EG_IP_2_EG_2_AUFTRAG
      FOREIGN KEY (EG2A_ID)
      REFERENCES T_EG_2_AUFTRAG (ID);

create SEQUENCE S_T_EG_IP_0 start with 1;
grant select on S_T_EG_IP_0 to public;

-- DB-Grants definieren
grant select, insert, update on T_EG_IP to R_HURRICAN_USER;
grant select on T_EG_IP to R_HURRICAN_READ_ONLY;
commit;
