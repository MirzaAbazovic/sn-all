-- Erstellt die Tabelle T_ROUTING fuer statische Routen von Endgeraeten

-- zugehoerige sequence erstellen
CREATE SEQUENCE S_T_EG_ROUTING_0
START WITH 1
INCREMENT BY 1
MINVALUE 1
NOCACHE
NOCYCLE
NOORDER;

GRANT SELECT ON S_T_EG_ROUTING_0 TO PUBLIC;

-- Tabelle erstellen
CREATE TABLE T_EG_ROUTING
(
    ID              NUMBER(10)                    NOT NULL,
    EG2A_ID         NUMBER(10)                    NULL,
    NAME            VARCHAR2(200 BYTE)            NULL,
    NEXT_HOP        VARCHAR2(50 BYTE)             NOT NULL,
    PREFIX          VARCHAR2(50 BYTE)             NOT NULL,
    PREFIX_LENGTH   NUMBER(10)                    NOT NULL,
    BEMERKUNG       VARCHAR2(1000 BYTE)           NULL
);

-- constraints erstellen
ALTER TABLE T_EG_ROUTING ADD (
  CONSTRAINT PK_T_EG_ROUTING
 PRIMARY KEY
 (ID));

ALTER TABLE T_EG_ROUTING
 ADD CONSTRAINT FK_T_EG_ROUTING_EG2A
 FOREIGN KEY (EG2A_ID)
 REFERENCES T_EG_2_AUFTRAG (ID)
    DEFERRABLE INITIALLY IMMEDIATE;

COMMENT ON COLUMN T_EG_ROUTING.BEMERKUNG IS 'Free text field for comments';



