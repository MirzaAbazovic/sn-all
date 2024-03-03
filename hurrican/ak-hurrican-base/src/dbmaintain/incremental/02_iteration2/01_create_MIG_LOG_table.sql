BEGIN
    execute immediate('ALTER TABLE MIG_LOG DROP PRIMARY KEY CASCADE');
EXCEPTION
   WHEN OTHERS THEN
      NULL;
END;
/
BEGIN
    execute immediate('DROP TABLE MIG_LOG CASCADE CONSTRAINTS');
EXCEPTION
   WHEN OTHERS THEN
      NULL;
END;
/
CREATE TABLE MIG_LOG
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
  LOG_UPD_USERNAME   VARCHAR2(50 BYTE)
)
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
      ( ENABLE      STORAGE IN ROW
        CHUNK       8192
        RETENTION
        NOCACHE
        INDEX       (
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


BEGIN
    execute immediate('DROP SEQUENCE SEQ_MIG_LOG');
EXCEPTION
   WHEN OTHERS THEN
      NULL;
END;
/

CREATE SEQUENCE SEQ_MIG_LOG
INCREMENT BY 1 START WITH 1000
/


CREATE OR REPLACE TRIGGER TRG_MIG_LOG
BEFORE INSERT OR UPDATE ON MIG_LOG
FOR EACH ROW
DECLARE
    iCounter MIG_LOG.LOG_ID%TYPE;
    cannot_change_counter EXCEPTION;
BEGIN
    IF INSERTING THEN
        IF :new.LOG_ID is NULL THEN
            Select SEQ_MIG_LOG.NEXTVAL INTO iCounter FROM Dual;
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
ALTER TABLE MIG_LOG ADD (
  PRIMARY KEY
 (LOG_ID)
    USING INDEX
    PCTFREE    10
    INITRANS   2
    MAXTRANS   255
    STORAGE    (
                INITIAL          64K
                MINEXTENTS       1
                MAXEXTENTS       UNLIMITED
                PCTINCREASE      0
               ));
/

COMMIT;
/

