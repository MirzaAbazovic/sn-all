
CREATE TABLE T_CB_VORGANG_SUB_ORDER (
    ID NUMBER(10) NOT NULL,
    CB_VORGANG_ID NUMBER(10),
    AUFTRAG_ID NUMBER(10) NOT NULL,
    DTAG_PORT VARCHAR2(10) NOT NULL
);

COMMENT ON TABLE T_CB_VORGANG_SUB_ORDER IS 'Tabelle protokolliert die Sub-Auftraege fuer eine TAL-Klammerung';
COMMENT ON COLUMN T_CB_VORGANG_SUB_ORDER.DTAG_PORT
      IS 'Angabe des bestellten DTAG Ports';
COMMENT ON COLUMN T_CB_VORGANG_SUB_ORDER.AUFTRAG_ID
      IS 'Referenz auf den techn. Auftrag';

ALTER TABLE T_CB_VORGANG_SUB_ORDER ADD CONSTRAINT PK_T_CBV_SUB_ORDER PRIMARY KEY (ID);

CREATE INDEX IX_FK_CBVSUB_2_AUFTRAG ON T_CB_VORGANG_SUB_ORDER (AUFTRAG_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_CB_VORGANG_SUB_ORDER ADD CONSTRAINT FK_CBVSUB_2_AUFTRAG
  FOREIGN KEY (AUFTRAG_ID) REFERENCES T_AUFTRAG (ID);

CREATE INDEX IX_FK_CBVSUB_2_CBV ON T_CB_VORGANG_SUB_ORDER (CB_VORGANG_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_CB_VORGANG_SUB_ORDER ADD CONSTRAINT FK_CBVSUB_2_CBV
  FOREIGN KEY (CB_VORGANG_ID) REFERENCES T_CB_VORGANG (ID);

alter table T_CB_VORGANG_SUB_ORDER add VERSION NUMBER(18) DEFAULT 0 NOT NULL;

create SEQUENCE S_T_CB_VORGANG_SUB_ORDER_0 start with 1;
grant select on S_T_CB_VORGANG_SUB_ORDER_0 to public;

GRANT SELECT ON T_CB_VORGANG_SUB_ORDER TO R_HURRICAN_READ_ONLY;
GRANT SELECT, INSERT, UPDATE ON T_CB_VORGANG_SUB_ORDER TO R_HURRICAN_USER;
