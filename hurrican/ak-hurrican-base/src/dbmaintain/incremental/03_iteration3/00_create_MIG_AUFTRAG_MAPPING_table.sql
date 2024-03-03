--
-- Legt eine Tabelle für die Zuordnung zwischen alten KuP Auftrags-Ids und neuen Hurrican Auftrags-Ids an
--

BEGIN
    execute immediate('ALTER TABLE MIG_AUFTRAG_MAPPING DROP PRIMARY KEY CASCADE');
EXCEPTION
   WHEN OTHERS THEN
      NULL;
END;
/

BEGIN
    execute immediate('DROP TABLE MIG_AUFTRAG_MAPPING CASCADE CONSTRAINTS');
EXCEPTION
   WHEN OTHERS THEN
      NULL;
END;
/

CREATE TABLE MIG_AUFTRAG_MAPPING
(
  ID                  NUMBER(10),
  AUFTRAG_ID_KUP      NUMBER(11),
  AUFTRAG_ID_HURRICAN NUMBER(10),
  INSERT_TIMESTAMP    DATE,
  INSERT_USERNAME     VARCHAR2(50 BYTE),
  UPDATE_TIMESTAMP    DATE,
  UPDATE_USERNAME     VARCHAR2(50 BYTE)
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
NOCACHE
NOPARALLEL
MONITORING;
/

BEGIN
    execute immediate('DROP SEQUENCE SEQ_MIG_AUFTRAG_MAPPING');
EXCEPTION
   WHEN OTHERS THEN
      NULL;
END;
/

CREATE SEQUENCE SEQ_MIG_AUFTRAG_MAPPING
INCREMENT BY 1 START WITH 1000
/

CREATE OR REPLACE TRIGGER TRG_MIG_AUFTRAG_MAPPING
BEFORE INSERT OR UPDATE ON MIG_AUFTRAG_MAPPING
FOR EACH ROW
DECLARE
    iCounter MIG_AUFTRAG_MAPPING.ID%TYPE;
    cannot_change_counter EXCEPTION;
BEGIN
    IF INSERTING THEN
        IF :new.ID is NULL THEN
            Select SEQ_MIG_AUFTRAG_MAPPING.NEXTVAL INTO iCounter FROM Dual;
           :new.ID := iCounter;
        END IF;
    END IF;

    IF :new.INSERT_TIMESTAMP IS NULL THEN
      :new.INSERT_TIMESTAMP  := SYSDATE;
    END IF;
   IF :new.INSERT_USERNAME IS NULL THEN
       :new.INSERT_USERNAME := USER;
    END IF;

    IF UPDATING THEN
        IF NOT (:new.ID = :old.ID) THEN
            RAISE cannot_change_counter;
        END IF;

       :new.UPDATE_TIMESTAMP  := SYSDATE;
       :new.UPDATE_USERNAME := USER;

    END IF;
EXCEPTION
     WHEN cannot_change_counter THEN
         raise_application_error(-20000, 'Cannot Change Counter Value');
END;
/
ALTER TABLE MIG_AUFTRAG_MAPPING ADD (
  PRIMARY KEY
 (ID)
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
