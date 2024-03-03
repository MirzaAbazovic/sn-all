--ALTER TABLE T_AUFTRAG_UMTS DROP PRIMARY KEY CASCADE;

--DROP TABLE T_AUFTRAG_UMTS CASCADE CONSTRAINTS;

CREATE TABLE T_AUFTRAG_UMTS
(
  ID                   NUMBER(10)             NOT NULL,
  AUFTRAG_ID           NUMBER(10)             NOT NULL,
  GUELTIG_VON          DATE                   NOT NULL,
  GUELTIG_BIS          DATE                   NOT NULL,
  USERW                VARCHAR2(25)           NOT NULL,
  MOBILFUNKANBIETER    VARCHAR2(32),
  SIM_KARTENNUMMER     VARCHAR2(24),
  MOBILFUNKRUFNUMMER   VARCHAR2(24),
  RAHMENVERTRAGSNUMMER VARCHAR2(15),
  APN                  VARCHAR2(64),
  VERSION              NUMBER(18)   DEFAULT 0 NOT NULL
);

CREATE INDEX IX_FK_UMTS_2_AUFTRAG ON T_AUFTRAG_UMTS (AUFTRAG_ID);

CREATE UNIQUE INDEX PK_T_AUFTRAG_UMTS ON T_AUFTRAG_UMTS (ID);

ALTER TABLE T_AUFTRAG_UMTS ADD (
  CONSTRAINT PK_T_AUFTRAG_UMTS
  PRIMARY KEY (ID)
  USING INDEX PK_T_AUFTRAG_UMTS);

ALTER TABLE T_AUFTRAG_UMTS ADD (
  CONSTRAINT FK_UMTS_2_AUFTRAG
  FOREIGN KEY (AUFTRAG_ID)
  REFERENCES T_AUFTRAG (ID));

GRANT SELECT ON T_AUFTRAG_UMTS TO R_HURRICAN_READ_ONLY;

GRANT INSERT, SELECT, UPDATE ON T_AUFTRAG_UMTS TO R_HURRICAN_USER;

--DROP SEQUENCE S_T_AUFTRAG_UMTS_0;

CREATE SEQUENCE S_T_AUFTRAG_UMTS_0
  START WITH 7793
  MAXVALUE 999999999999999999999999999
  MINVALUE 1
  NOCYCLE
  CACHE 20
  NOORDER;


GRANT SELECT ON S_T_AUFTRAG_UMTS_0 TO PUBLIC;