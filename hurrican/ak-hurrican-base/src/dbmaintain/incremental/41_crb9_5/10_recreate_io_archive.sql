ALTER TABLE T_IO_ARCHIVE
 DROP PRIMARY KEY CASCADE;

DROP TABLE T_IO_ARCHIVE CASCADE CONSTRAINTS;

CREATE TABLE T_IO_ARCHIVE
(
  ID                      NUMBER(19)            NOT NULL,
  VERSION                 NUMBER(19)            DEFAULT 0                     NOT NULL,
  IO_TYPE                 VARCHAR2(10 BYTE)     DEFAULT NULL                  NOT NULL,
  WITA_EXT_ORDER_NO       VARCHAR2(20 BYTE)     NOT NULL,
  REQUEST_TIMEST          DATE                  NOT NULL,
  RESPONSE_TIMEST         DATE,
  TIMESTAMP_SENT          DATE                  NOT NULL,
  REQUEST_GESCHAEFTSFALL  VARCHAR2(20 BYTE)     NOT NULL,
  REQUEST_MELDUNGSCODE    VARCHAR2(30 BYTE),
  REQUEST_MELDUNGSTEXT    VARCHAR2(100 BYTE),
  RESPONSE_MELDUNGSCODE   VARCHAR2(30 BYTE),
  RESPONSE_MELDUNGSTEXT   VARCHAR2(100 BYTE),
  REQUEST_MELDUNGSTYP     VARCHAR2(30 BYTE),
  REQUEST_XML             CLOB                  NOT NULL,
  RESPONSE_XML            CLOB
)
LOB (REQUEST_XML) STORE AS (
  TABLESPACE T_HURRICAN
  ENABLE       STORAGE IN ROW
  CHUNK       8192
  RETENTION
  NOCACHE
  LOGGING
  INDEX       (
        TABLESPACE T_HURRICAN
        STORAGE    (
                    INITIAL          64K
                    MINEXTENTS       1
                    MAXEXTENTS       UNLIMITED
                    PCTINCREASE      0
                    BUFFER_POOL      DEFAULT
                   ))
      STORAGE    (
                  INITIAL          64K
                  MINEXTENTS       1
                  MAXEXTENTS       UNLIMITED
                  PCTINCREASE      0
                  BUFFER_POOL      DEFAULT
                 ))
LOB (RESPONSE_XML) STORE AS (
  TABLESPACE T_HURRICAN
  ENABLE       STORAGE IN ROW
  CHUNK       8192
  RETENTION
  NOCACHE
  LOGGING
  INDEX       (
        TABLESPACE T_HURRICAN
        STORAGE    (
                    INITIAL          64K
                    MINEXTENTS       1
                    MAXEXTENTS       UNLIMITED
                    PCTINCREASE      0
                    BUFFER_POOL      DEFAULT
                   ))
      STORAGE    (
                  INITIAL          64K
                  MINEXTENTS       1
                  MAXEXTENTS       UNLIMITED
                  PCTINCREASE      0
                  BUFFER_POOL      DEFAULT
                 ))
TABLESPACE T_HURRICAN
PCTUSED    0
PCTFREE    10
INITRANS   1
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
LOGGING
NOCOMPRESS
NOCACHE
NOPARALLEL
MONITORING;

COMMENT ON TABLE T_IO_ARCHIVE IS 'I/O Archive fuer die ein-/ausgehenden SOAP Messages von WITA';
COMMENT ON COLUMN T_IO_ARCHIVE.WITA_EXT_ORDER_NO IS 'externe Auftragsnummer der WITA-Schnittstelle';
COMMENT ON COLUMN T_IO_ARCHIVE.REQUEST_TIMEST IS 'Zeitpunkt, wann der Request erstellt wurde';
COMMENT ON COLUMN T_IO_ARCHIVE.RESPONSE_TIMEST IS 'Zeitpunkt, wann der Response uebermittelt wurde';


ALTER TABLE T_IO_ARCHIVE ADD (
  PRIMARY KEY
  (ID)
  USING INDEX
    TABLESPACE T_HURRICAN
    PCTFREE    10
    INITRANS   2
    MAXTRANS   255
    STORAGE    (
                INITIAL          64K
                MINEXTENTS       1
                MAXEXTENTS       UNLIMITED
                PCTINCREASE      0
               ));

GRANT SELECT ON T_IO_ARCHIVE TO R_HURRICAN_READ_ONLY;

GRANT INSERT, SELECT, UPDATE ON T_IO_ARCHIVE TO R_HURRICAN_USER;
