--
-- SQL-Script, das die Tabellen fuer die ACLss auf den Endgeraeten
-- erzeugt und damit eine n:m Beziehung zulaesst.
--

CREATE TABLE T_EG_ACL (
  ID                NUMBER(10)                  NOT NULL,
  ACL_TEXT           VARCHAR2(255 BYTE)         NOT NULL,
  ACL_NAME           VARCHAR2(255 BYTE)         NOT NULL,
  ACL_ROUTERTYP      VARCHAR2(50 BYTE)
);

COMMENT ON TABLE T_EG_ACL IS 'Tabelle um die ACLs fuer die Endgeraetekonfiguration zu halten';
commit;

ALTER TABLE T_EG_ACL ADD CONSTRAINT PK_T_EG_ACL PRIMARY KEY (ID);
commit;

-- Business key is ACL_TEXT
ALTER TABLE T_EG_ACL
  ADD CONSTRAINT UK_T_EG_ACL_BK
      UNIQUE (ACL_TEXT);

create SEQUENCE S_T_EG_ACL_0 start with 1;
grant select on S_T_EG_ACL_0 to public;

-- Mapping Table
CREATE TABLE T_EG_CONFIG_2_ACL (
  EG_CONFIG_ID             NUMBER(10)                  NOT NULL,
  EG_ACL_ID                NUMBER(10)                  NOT NULL
);
COMMENT ON TABLE T_EG_CONFIG_2_ACL IS 'Mapping EG_CONFIG zu EG_ACL, many to many';
ALTER TABLE T_EG_CONFIG_2_ACL ADD CONSTRAINT PK_T_EG_CONFIG_2_ACL PRIMARY KEY (EG_CONFIG_ID, EG_ACL_ID);
ALTER TABLE T_EG_CONFIG_2_ACL
  ADD CONSTRAINT FK_EG_CONFIG_2_ACL_2_EG_CONFIG
      FOREIGN KEY (EG_CONFIG_ID)
      REFERENCES T_EG_CONFIG (ID);
ALTER TABLE T_EG_CONFIG_2_ACL
  ADD CONSTRAINT FK_EG_CONFIG_2_ACL_2_EG_ACL
      FOREIGN KEY (EG_ACL_ID)
      REFERENCES T_EG_ACL (ID);


-- DB-Grants definieren
grant select, insert, update on T_EG_ACL to R_HURRICAN_USER;
grant select on T_EG_ACL to R_HURRICAN_READ_ONLY;
grant select, insert, update on T_EG_CONFIG_2_ACL to R_HURRICAN_USER;
grant select on T_EG_CONFIG_2_ACL to R_HURRICAN_READ_ONLY;
commit;
