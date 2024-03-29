CREATE TABLE T_MWF_PROJEKT
(
  ID               NUMBER(19)                   NOT NULL,
  VERSION          NUMBER(19)    DEFAULT 0      NOT NULL,
  PROJEKT_KENNER   VARCHAR2(30)                 NOT NULL,
  KOPPLUNG_KENNER  VARCHAR2(30)
);

ALTER TABLE T_MWF_PROJEKT ADD (
  CONSTRAINT T_MWF_PROJEKT_PK
  PRIMARY KEY
  (ID));

ALTER TABLE T_MWF_REQUEST
 ADD (PROJEKT_ID  NUMBER(19));

ALTER TABLE T_MWF_REQUEST ADD CONSTRAINT FK_MWF_REQUEST_PROJEKT
  FOREIGN KEY (PROJEKT_ID)
  REFERENCES T_MWF_PROJEKT (ID);

GRANT SELECT, INSERT, UPDATE ON T_MWF_PROJEKT to R_HURRICAN_USER;
GRANT SELECT ON T_MWF_PROJEKT to R_HURRICAN_READ_ONLY;

CREATE SEQUENCE S_T_MWF_PROJEKT_0
  START WITH 1
  MAXVALUE 999999999999999999999999999
  MINVALUE 1
  NOCYCLE
  CACHE 20
  NOORDER;


GRANT SELECT ON S_T_MWF_PROJEKT_0 TO PUBLIC;
