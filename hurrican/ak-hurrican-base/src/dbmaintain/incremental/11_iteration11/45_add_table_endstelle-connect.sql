-- notwendige Tabelle f�r Endstelle-Connect
CREATE TABLE T_ENDSTELLE_CONNECT
(
  ID              NUMBER(10)                    NOT NULL,
  VERSION         NUMBER(18)                    NOT NULL,
  ENDSTELLE_ID    NUMBER(10)                    NOT NULL,
  GEBAEUDE        VARCHAR2(200 BYTE),
  ETAGE           VARCHAR2(200 BYTE),
  RAUM            VARCHAR2(200 BYTE),
  SCHRANK         VARCHAR2(200 BYTE),
  UEBERGABE       VARCHAR2(200 BYTE),
  BANDBREITE      VARCHAR2(200 BYTE),
  SCHNITTSTELLE   VARCHAR2(200 BYTE),
  EINSTELLUNG     VARCHAR2(200 BYTE),
  ROUTERINFO      VARCHAR2(200 BYTE),
  ROUTERTYP       VARCHAR2(200 BYTE),
  BEMERKUNG       VARCHAR2(4000 BYTE)
);

-- Kommentare
COMMENT ON TABLE T_ENDSTELLE_CONNECT IS 'Tabelle, um Connect-Daten der Endstelle zu definieren (1:1 zu T_ENDSTELLE)';

-- Constraints
ALTER TABLE T_ENDSTELLE_CONNECT ADD (CONSTRAINT T_ENDSTELLE_CONNECT_PK PRIMARY KEY (ID));

ALTER TABLE T_ENDSTELLE_CONNECT ADD (CONSTRAINT FK_ENDST_CONNECT_2_ENDST
    FOREIGN KEY (ENDSTELLE_ID) REFERENCES T_ENDSTELLE (ID)
);

-- Sequenz
CREATE SEQUENCE S_T_ENDSTELLE_CONNECT_0;

-- Indexes
CREATE UNIQUE INDEX IX_FK_ENDST_CONNECT_2_ENDST ON T_ENDSTELLE_CONNECT (ENDSTELLE_ID);

-- Rechte f�r Tabelle und Sequenz
GRANT SELECT, INSERT, UPDATE, DELETE ON T_ENDSTELLE_CONNECT TO R_HURRICAN_USER;
GRANT SELECT ON T_ENDSTELLE_CONNECT TO R_HURRICAN_READ_ONLY;
GRANT SELECT ON S_T_ENDSTELLE_CONNECT_0 TO PUBLIC;