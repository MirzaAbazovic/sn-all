-- Zusatz-Tabelle fuer MVS-Daten

create table T_AUFTRAG_MVS (
    ID NUMBER(10) NOT NULL,
    AUFTRAG_ID NUMBER(10) NOT NULL,
    MVS_TYPE varchar2(10) NOT NULL,
    USERNAME varchar2(30) NOT NULL,
    PASSWORD varchar2(8) NOT NULL,
    MAIL varchar2(128),
    DOMAIN varchar2(30),
    SUBDOMAIN varchar2(30),
    VERSION NUMBER(18) DEFAULT 0 NOT NULL
);

ALTER TABLE T_AUFTRAG_MVS ADD CONSTRAINT PK_T_AUFTRAG_MVS PRIMARY KEY (ID);

CREATE INDEX IX_FK_MVS_2_AUFTRAG ON T_AUFTRAG_MVS (AUFTRAG_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_AUFTRAG_MVS ADD CONSTRAINT FK_MVS_2_AUFTRAG
  FOREIGN KEY (AUFTRAG_ID) REFERENCES T_AUFTRAG (ID);


create SEQUENCE S_T_AUFTRAG_MVS_0 start with 1;
grant select on S_T_AUFTRAG_MVS_0 to public;

GRANT SELECT ON T_AUFTRAG_MVS TO R_HURRICAN_READ_ONLY;
GRANT SELECT, INSERT, UPDATE ON T_AUFTRAG_MVS TO R_HURRICAN_USER;
