-- Erstellt einige billingrelevante Tabellen, so dass auch auf Datenbanken
-- ohne Billingdaten ein CDR-Accounting durchgefuehrt werden kann.

CREATE TABLE A_PRICE_SUM_BACKUP
(
  BULK_TYPE  VARCHAR2(20 BYTE)                  NOT NULL,
  BT_SIZE    NUMBER(10)                         NOT NULL,
  USERW      VARCHAR2(32 BYTE)                  NOT NULL,
  DATEW      DATE                               NOT NULL
)
/


CREATE UNIQUE INDEX PK_A_PRICE_SUM_BACKUP ON A_PRICE_SUM_BACKUP
(BULK_TYPE)
LOGGING
TABLESPACE TAIFUN_INDEX
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL
/


ALTER TABLE A_PRICE_SUM_BACKUP ADD (
  CONSTRAINT PK_A_PRICE_SUM_BACKUP
  PRIMARY KEY
  (BULK_TYPE)
  USING INDEX PK_A_PRICE_SUM_BACKUP)
/

ALTER TABLE A_PRICE_SUM_BACKUP ADD (
  CONSTRAINT FK_A_PRICE_SUM_BACKUP_0
  FOREIGN KEY (BULK_TYPE)
  REFERENCES DBU_BULK (BULK_TYPE)
  ON DELETE CASCADE)
/
