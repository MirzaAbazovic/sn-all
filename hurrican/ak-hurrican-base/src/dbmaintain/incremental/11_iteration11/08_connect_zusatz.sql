-- Zusatz-Tabelle fuer Connect-Daten

create table T_AUFTRAG_CONNECT (
    ID NUMBER(10) NOT NULL
  , AUFTRAG_ID NUMBER(10) NOT NULL
  , GUELTIG_VON DATE NOT NULL
  , GUELTIG_BIS DATE NOT NULL
  , VERSION NUMBER(18) DEFAULT 0 NOT NULL
);

ALTER TABLE T_AUFTRAG_CONNECT ADD CONSTRAINT PK_T_AUFTRAG_CONNECT PRIMARY KEY (ID);

CREATE INDEX IX_FK_CONNECT_2_AUFTRAG ON T_AUFTRAG_CONNECT (AUFTRAG_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_AUFTRAG_CONNECT ADD CONSTRAINT FK_CONNECT_2_AUFTRAG
  FOREIGN KEY (AUFTRAG_ID) REFERENCES T_AUFTRAG (ID);


create SEQUENCE S_T_AUFTRAG_CONNECT_0 start with 1;
grant select on S_T_AUFTRAG_CONNECT_0 to public;

GRANT SELECT ON T_AUFTRAG_CONNECT TO R_HURRICAN_READ_ONLY;
GRANT SELECT, INSERT, UPDATE ON T_AUFTRAG_CONNECT TO R_HURRICAN_USER;

