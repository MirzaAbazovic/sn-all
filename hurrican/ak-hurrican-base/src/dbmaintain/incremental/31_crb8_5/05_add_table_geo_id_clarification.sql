CREATE TABLE T_GEO_ID_CLARIFICATION
(
  ID                     NUMBER(19)             NOT NULL,
  GEO_ID                 NUMBER(19)             NOT NULL,
  STATUS_REF_ID          NUMBER(10)             NOT NULL,
  TYPE_REF_ID            NUMBER(10)             NOT NULL,
  INFO                   VARCHAR2(255),
  USERW                  VARCHAR2(15),
  DATEW                  DATE,
  VERSION                NUMBER(18)   DEFAULT 0 NOT NULL
);

COMMENT ON TABLE T_GEO_ID_CLARIFICATION IS 'Tabelle haelt die Kl�rungsliste die durch den kontinuierlichen Sync mit Vento entstehen kann';

COMMENT ON COLUMN T_GEO_ID_CLARIFICATION.ID IS 'ID des Datensatzes';
COMMENT ON COLUMN T_GEO_ID_CLARIFICATION.GEO_ID IS 'FK Referenz in die Geo ID Cachingtabelle';
COMMENT ON COLUMN T_GEO_ID_CLARIFICATION.STATUS_REF_ID IS 'ID des Kl�rungsstatus aus der T_REFERENCE';
COMMENT ON COLUMN T_GEO_ID_CLARIFICATION.TYPE_REF_ID IS 'ID des Kl�rungstyps aus der T_REFERENCE';
COMMENT ON COLUMN T_GEO_ID_CLARIFICATION.INFO IS 'Daten die die Kl�rung n�her beschreiben';
COMMENT ON COLUMN T_GEO_ID_CLARIFICATION.USERW IS 'Schreibender Zugriff des letzten Benutzers';
COMMENT ON COLUMN T_GEO_ID_CLARIFICATION.DATEW IS 'Datum des letzten schreibenden Zugriffs';

create SEQUENCE S_T_GEO_ID_CLARIFICATION_0 start with 1;
grant select on S_T_GEO_ID_CLARIFICATION_0 to public;

ALTER TABLE T_GEO_ID_CLARIFICATION ADD CONSTRAINT PK_T_GEO_ID_CLARIFICATION PRIMARY KEY (ID);

CREATE INDEX IX_FK_GEOIDCLF_2_GEOID ON T_GEO_ID_CLARIFICATION (GEO_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_GEO_ID_CLARIFICATION ADD CONSTRAINT FK_GEOIDCLF_2_GEOID
  FOREIGN KEY (GEO_ID) REFERENCES T_GEO_ID_CACHE (ID);

CREATE INDEX IX_FK_GIDCS_2_REF ON T_GEO_ID_CLARIFICATION (STATUS_REF_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_GEO_ID_CLARIFICATION ADD CONSTRAINT FK_GIDCS_2_REF
  FOREIGN KEY (STATUS_REF_ID) REFERENCES T_REFERENCE (ID);

CREATE INDEX IX_FK_GIDCT_2_REF ON T_GEO_ID_CLARIFICATION (TYPE_REF_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_GEO_ID_CLARIFICATION ADD CONSTRAINT FK_GIDCT_2_REF
  FOREIGN KEY (TYPE_REF_ID) REFERENCES T_REFERENCE (ID);

create trigger TRBIU_GEOIDCLF BEFORE INSERT or UPDATE on T_GEO_ID_CLARIFICATION
for each row
begin
 -- TIMESTAMP setzen
 SELECT CURRENT_TIMESTAMP into :new.DATEW FROM dual;
end;
/

GRANT SELECT ON T_GEO_ID_CLARIFICATION TO R_HURRICAN_READ_ONLY;
GRANT SELECT, INSERT, UPDATE ON T_GEO_ID_CLARIFICATION TO R_HURRICAN_USER;
