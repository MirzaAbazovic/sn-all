-- Tabelle zunaechst verwerfen
DROP TABLE T_EG_2_EG_TYPE;

-- Tabelle neu anlegen
CREATE TABLE T_EG_2_EG_TYPE (
  ID                   NUMBER(10)                  NOT NULL,
  EG_ID                NUMBER(10)                  NOT NULL,
  EG_TYPE_ID           NUMBER(10)                  NOT NULL,
  USERW                VARCHAR2(15),
  DATEW                DATE,
  VERSION              NUMBER(18)                  DEFAULT 0 NOT NULL
);

COMMENT ON TABLE T_EG_2_EG_TYPE IS 'Mapping T_EG zu T_EG_TYPES, many to many';
COMMENT ON COLUMN T_EG_2_EG_TYPE.ID IS 'ID des Datensatzes';
COMMENT ON COLUMN T_EG_2_EG_TYPE.EG_ID IS 'Rererenziert Endgeraet';
COMMENT ON COLUMN T_EG_2_EG_TYPE.EG_TYPE_ID IS 'Rererenziert Endgeraetetyp';
COMMENT ON COLUMN T_EG_2_EG_TYPE.USERW IS 'Schreibender Zugriff des letzten Benutzers';
COMMENT ON COLUMN T_EG_2_EG_TYPE.DATEW IS 'Datum des letzten schreibenden Zugriffs';

CREATE SEQUENCE S_T_EG_2_EG_TYPE_0 start with 1;
GRANT SELECT ON S_T_EG_2_EG_TYPE_0 to public;

ALTER TABLE T_EG_2_EG_TYPE ADD CONSTRAINT PK_T_EG_2_T_EG_TYPE PRIMARY KEY (ID);

CREATE TRIGGER TRBIU_EG_2_EG_TYPE BEFORE INSERT or UPDATE on T_EG_2_EG_TYPE
for each row
BEGIN
 -- TIMESTAMP setzen
 SELECT CURRENT_TIMESTAMP INTO :new.DATEW FROM dual;
END;
/

CREATE INDEX IX_FK_EG_2_EG_2_EG_TYPE ON T_EG_2_EG_TYPE (EG_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_EG_2_EG_TYPE
  ADD CONSTRAINT FK_EG_2_EG_2_EG_TYPE
      FOREIGN KEY (EG_ID)
      REFERENCES T_EG (ID);

CREATE INDEX IX_FK_EG_TYPE_2_T_EG_2_EG_TYPE ON T_EG_2_EG_TYPE (EG_TYPE_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_EG_2_EG_TYPE
  ADD CONSTRAINT FK_EG_TYPE_2_T_EG_2_EG_TYPE
      FOREIGN KEY (EG_TYPE_ID)
      REFERENCES T_EG_TYPE (ID);

CREATE UNIQUE INDEX IUQ_T_EG_2_EG_TYPE ON T_EG_2_EG_TYPE (EG_ID, EG_TYPE_ID);

-- DB-Grants ausfuehren
grant select, insert, update, delete on T_EG_2_EG_TYPE to R_HURRICAN_USER;
grant select on T_EG_2_EG_TYPE to R_HURRICAN_READ_ONLY;
