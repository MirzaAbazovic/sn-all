--
-- Legt eine Tabelle f�r Migrations-Ergebnisse an
--

BEGIN
    execute immediate('ALTER TABLE HURRICAN_MIG_RESULT DROP PRIMARY KEY CASCADE');
EXCEPTION
   WHEN OTHERS THEN
      NULL;
END;
/


BEGIN
    execute immediate('DROP TABLE HURRICAN_MIG_RESULT CASCADE CONSTRAINTS');
EXCEPTION
   WHEN OTHERS THEN
      NULL;
END;
/

CREATE TABLE HURRICAN_MIG_RESULT
(
  ID                NUMBER(10),
  MIGRATION_NAME    VARCHAR2(50 BYTE),
  MIGRATION_SUITE   VARCHAR2(50 BYTE),
  SUCCESS           VARCHAR2(50 BYTE),
  COUNTER           NUMBER(10),
  MIGRATED          NUMBER(10),
  INFO              NUMBER(10),
  WARNINGS          NUMBER(10),
  BAD_DATA          NUMBER(10),
  ERRORS            NUMBER(10),
  SKIPPED           NUMBER(10),
  INSERT_TIMESTAMP  DATE,
  INSERT_USERNAME   VARCHAR2(50 BYTE),
  UPDATE_TIMESTAMP  DATE,
  UPDATE_USERNAME   VARCHAR2(50 BYTE)
) NOLOGGING;


BEGIN
    execute immediate('DROP SEQUENCE SEQ_HURRICAN_MIG_RESULT');
EXCEPTION
   WHEN OTHERS THEN
      NULL;
END;
/

CREATE SEQUENCE SEQ_HURRICAN_MIG_RESULT INCREMENT BY 1 START WITH 1000;

CREATE OR REPLACE TRIGGER TRG_HURRICAN_MIG_RESULT
BEFORE INSERT OR UPDATE ON HURRICAN_MIG_RESULT
FOR EACH ROW
DECLARE
    iCounter HURRICAN_MIG_RESULT.ID%TYPE;
    cannot_change_counter EXCEPTION;
BEGIN
    IF INSERTING THEN
        IF :new.ID is NULL THEN
            Select SEQ_HURRICAN_MIG_RESULT.NEXTVAL INTO iCounter FROM Dual;
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

ALTER TABLE HURRICAN_MIG_RESULT ADD (PRIMARY KEY (ID));
