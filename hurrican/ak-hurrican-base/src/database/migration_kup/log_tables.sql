--
-- Migration KuP / Hurrican
-- ===========================================================================
-- Inhalt:
--   - Anlage der Log-Tables fuer die Migration (auf der Ziel-DB)
--

CREATE TABLE MIG_SEVERITY (
       ID NUMBER(10) NOT NULL
     , SEVERITY VARCHAR2(50) NOT NULL
);
ALTER TABLE MIG_SEVERITY ADD CONSTRAINT PK_MIG_SEVERITY PRIMARY KEY (ID);

CREATE TABLE MIG_LOG (
       ID NUMBER(10) NOT NULL
     , SCRIPT_NAME VARCHAR2(255)
     , TIMEST DATE
     , SRC_TABLE VARCHAR2(100)
     , SRC_VALUE VARCHAR2(200)
     , DEST_TABLE VARCHAR2(100)
     , DEST_VALUE VARCHAR2(200)
     , SEVERITY NUMBER(10)
     , MESSAGE VARCHAR2(4000)
     , EXCEPTION_MSG VARCHAR2(4000)
);
ALTER TABLE MIG_LOG ADD CONSTRAINT PK_MIG_LOG PRIMARY KEY (ID);

CREATE INDEX IX_FK_MIGLOG_2_SEVERITY
	ON MIG_LOG (SEVERITY) TABLESPACE "I_HURRICAN";
	
ALTER TABLE MIG_LOG
  ADD CONSTRAINT FK_MIG_LOG_1
      FOREIGN KEY (SEVERITY)
      REFERENCES MIG_SEVERITY (ID);
      
create sequence S_MIG_LOG_0 start with 1;
grant select on S_MIG_LOG_0 to public;

drop trigger TRBI_MIG_LOG;
create trigger TRBI_MIG_LOG BEFORE INSERT on MIG_LOG
 for each row
  when (new.ID is null)
   begin
    SELECT S_MIG_LOG_0.nextval into :new.ID FROM dual;
    SELECT CURRENT_TIMESTAMP into :new.TIMEST FROM dual;
   end;
/
commit;

-- Basisdaten fuer MIG_SEVERITY anlegen
insert into MIG_SEVERITY (ID, SEVERITY) values (0, 'OK');
insert into MIG_SEVERITY (ID, SEVERITY) values (1, 'INFO');
insert into MIG_SEVERITY (ID, SEVERITY) values (2, 'WARN');
insert into MIG_SEVERITY (ID, SEVERITY) values (3, 'ERROR');
commit;

