CREATE TABLE T_SMS_CONFIG
(
  ID                     NUMBER(19,0)            NOT NULL,
  VERSION                NUMBER(19,0) DEFAULT 0  NOT NULL,
  SCHNITTSTELLE          VARCHAR2(10)            NOT NULL,
  GESCHAEFTSFALL_TYP     VARCHAR2(40)            NOT NULL,
  MELDUNG_TYP            VARCHAR2(40)            NOT NULL,
  MONTAGE                VARCHAR2(10)            NOT NULL,
  AENDERUNGSKENNZEICHEN  VARCHAR2(20)            NOT NULL,
  TEMPLATE_TEXT          VARCHAR2(320)           NOT NULL,
  CONSTRAINT C_MONTAGE CHECK (MONTAGE IN ('YES', 'NO', 'IGNORE')),
  PRIMARY KEY (ID)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON T_SMS_CONFIG TO R_HURRICAN_USER;
GRANT SELECT ON T_SMS_CONFIG TO R_HURRICAN_READ_ONLY;

CREATE SEQUENCE S_T_SMS_CONFIG_0;
GRANT SELECT ON S_T_SMS_CONFIG_0 TO PUBLIC;
