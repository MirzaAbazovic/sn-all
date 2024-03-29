
CREATE TABLE T_CB_VORGANG_RUFNUMMERN (
    ID NUMBER(10) NOT NULL,
    CB_VORGANG_ID NUMBER(10) NOT NULL,
    RUFNUMMER_ID NUMBER(19) NOT NULL
);

ALTER TABLE T_CB_VORGANG_RUFNUMMERN ADD (
  CONSTRAINT T_CB_VORGANG_RUFNUMMERN_PK
  PRIMARY KEY
  (ID));

ALTER TABLE T_CB_VORGANG_RUFNUMMERN ADD (
  CONSTRAINT FK_CB_VORGANG_RUFNUMMERN
  FOREIGN KEY (CB_VORGANG_ID)
  REFERENCES T_CB_VORGANG (ID));

GRANT SELECT, INSERT, UPDATE ON T_CB_VORGANG_RUFNUMMERN TO R_HURRICAN_USER;
GRANT SELECT ON T_CB_VORGANG_RUFNUMMERN TO R_HURRICAN_READ_ONLY;

CREATE SEQUENCE S_T_CB_VORGANG_RUFNUMMERN_0
   START WITH 1;

GRANT SELECT ON S_T_CB_VORGANG_RUFNUMMERN_0 TO PUBLIC;