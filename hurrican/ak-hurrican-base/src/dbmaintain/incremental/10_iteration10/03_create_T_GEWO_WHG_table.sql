--
-- Erzeuge Tablle für GEWOFAG-Wohnungen
--

CREATE TABLE T_GEWO_WHG
(
  ID      NUMBER(11)                        NOT NULL,
  VERSION  NUMBER(20)                       NOT NULL,
  SL_ID   NUMBER(10)                        NOT NULL,
  EQ_ID   NUMBER(10),
  NAME    VARCHAR2(10 BYTE)                 NOT NULL,
  HAUSNUMMER VARCHAR2(10 BYTE)              NOT NULL,
  ETAGE   VARCHAR2(15 BYTE)                 NOT NULL,
  LAGE    VARCHAR2(15 BYTE)                 NOT NULL,
  TAE     VARCHAR2(50 BYTE)                 NOT NULL
);

COMMENT ON TABLE T_GEWO_WHG IS 'Tabelle enthaelt die Informationen ueber die Wohnungen der GEWOFAG';
COMMENT ON COLUMN T_GEWO_WHG.VERSION
      IS 'Hibernate Version';
COMMENT ON COLUMN T_GEWO_WHG.SL_ID
      IS 'ID der Strasse aus der Strassenliste';
COMMENT ON COLUMN T_GEWO_WHG.EQ_ID
      IS 'Equipment ID des Equipments mit Bucht-Leiste-Stift im Kellerraum';
COMMENT ON COLUMN T_GEWO_WHG.NAME
      IS 'Mietvertragsnummer';
COMMENT ON COLUMN T_GEWO_WHG.HAUSNUMMER
      IS 'Hausnummer, in der die Wohnung ist. Strasse via SL_ID';
COMMENT ON COLUMN T_GEWO_WHG.ETAGE
      IS 'Etage, in der die Wohnung ist';
COMMENT ON COLUMN T_GEWO_WHG.LAGE
      IS 'Lage der Wohnung';
COMMENT ON COLUMN T_GEWO_WHG.TAE
      IS 'Bezeichung der TAE in der Wohnung';

ALTER TABLE T_GEWO_WHG ADD (
  CONSTRAINT PK_GEWO_WHG
 PRIMARY KEY
 (ID));

ALTER TABLE T_GEWO_WHG
 ADD CONSTRAINT FK_GEWO_WHG_SL
 FOREIGN KEY (SL_ID)
 REFERENCES T_STRASSENLISTE (SL_ID)
    DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE T_GEWO_WHG
 ADD CONSTRAINT FK_GEWO_WHG_EQ
 FOREIGN KEY (EQ_ID)
 REFERENCES T_EQUIPMENT (EQ_ID)
    DEFERRABLE INITIALLY IMMEDIATE;

grant select, insert, update on T_GEWO_WHG to R_HURRICAN_USER;
grant select on T_GEWO_WHG to R_HURRICAN_READ_ONLY;

create SEQUENCE S_T_GEWO_WHG_0 start with 1;
grant select on S_T_GEWO_WHG_0 to public;
