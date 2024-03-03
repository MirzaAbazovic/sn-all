
CREATE TABLE MIG_LOG_CRB6
(
  LOG_ID             NUMBER(10),
  SCRIPT_NAME        VARCHAR2(4000 BYTE),
  SRC_TABLE          VARCHAR2(4000 BYTE),
  SRC_VALUE          VARCHAR2(4000 BYTE),
  DEST_TABLE         VARCHAR2(4000 BYTE),
  DEST_VALUE         VARCHAR2(4000 BYTE),
  SEVERITY           VARCHAR2(4000 BYTE),
  MESSAGE            VARCHAR2(4000 BYTE),
  EXCEPTION_MSG      CLOB,
  LOG_TIMESTAMP      DATE,
  LOG_USERNAME       VARCHAR2(50 BYTE),
  LOG_UPD_TIMESTAMP  DATE,
  LOG_UPD_USERNAME   VARCHAR2(50 BYTE),
  MIGRESULT_ID       NUMBER(10)
)
TABLESPACE T_HURRICAN
PCTUSED    0
PCTFREE    10
INITRANS   1
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOLOGGING
NOCOMPRESS
LOB (EXCEPTION_MSG) STORE AS
      ( TABLESPACE  T_HURRICAN
        ENABLE      STORAGE IN ROW
        CHUNK       8192
        RETENTION
        NOCACHE
        INDEX       (
          TABLESPACE T_HURRICAN
          STORAGE    (
                      INITIAL          816K
                      NEXT             1
                      MINEXTENTS       1
                      MAXEXTENTS       UNLIMITED
                      PCTINCREASE      0
                      BUFFER_POOL      DEFAULT
                     ))
        STORAGE    (
                    INITIAL          16M
                    MINEXTENTS       1
                    MAXEXTENTS       UNLIMITED
                    PCTINCREASE      0
                    BUFFER_POOL      DEFAULT
                   )
      )
NOCACHE
NOPARALLEL
MONITORING;

create sequence SEQ_MIG_LOG_CRB6 start with 1;


CREATE OR REPLACE TRIGGER TRG_MIG_LOG_CRB6
BEFORE INSERT OR UPDATE ON MIG_LOG_CRB6 FOR EACH ROW
DECLARE
    iCounter MIG_LOG_CRB6.LOG_ID%TYPE;
    cannot_change_counter EXCEPTION;
BEGIN
    IF INSERTING THEN
        IF :new.LOG_ID is NULL THEN
            Select SEQ_MIG_LOG_CRB6.NEXTVAL INTO iCounter FROM Dual;
           :new.LOG_ID := iCounter;
        END IF;
    END IF;

    IF :new.LOG_TIMESTAMP IS NULL THEN
      :new.LOG_TIMESTAMP  := SYSDATE;
    END IF;
   IF :new.LOG_USERNAME IS NULL THEN
       :new.LOG_USERNAME := USER;
    END IF;

    IF UPDATING THEN
        IF NOT (:new.LOG_ID = :old.LOG_ID) THEN
            RAISE cannot_change_counter;
        END IF;

       :new.LOG_UPD_TIMESTAMP  := SYSDATE;
       :new.LOG_UPD_USERNAME := USER;

    END IF;
EXCEPTION
     WHEN cannot_change_counter THEN
         raise_application_error(-20000, 'Cannot Change Counter Value');
END;
/


ALTER TABLE MIG_LOG_CRB6 ADD (
  PRIMARY KEY
 (LOG_ID)
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

ALTER TABLE MIG_LOG_CRB6 ADD (
  FOREIGN KEY (MIGRESULT_ID)
 REFERENCES MIG_MIGRATIONRESULT (ID));

GRANT SELECT ON MIG_LOG_CRB6 TO R_HURRICAN_TECHNIKMUC;

GRANT SELECT ON MIG_LOG_CRB6 TO R_HURRICAN_TOOLS;
