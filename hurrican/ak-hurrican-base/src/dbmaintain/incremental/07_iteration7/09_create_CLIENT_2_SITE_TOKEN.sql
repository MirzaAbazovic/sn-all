--
-- Aufbau der neuen Tabellen fuer die IPSec Client-to-Site Tokens
--

CREATE TABLE T_CLIENT_2_SITE_TOKEN
(
  ID                   NUMBER(10)               NOT NULL,
  AUFTRAG_ID           NUMBER(10),
  SERIAL_NUMBER        VARCHAR2(50 BYTE),
  BEMERKUNG            VARCHAR2(250 BYTE),
  SAP_ORDER_ID         VARCHAR2(50 BYTE)        NOT NULL,
  LAUFZEIT_IN_MONATEN  NUMBER(10)               NOT NULL,
  LIEFERDATUM          DATE                     NOT NULL,
  BATTERIE_ENDE        DATE                     NOT NULL,
  RADIUS_ACCOUNT       VARCHAR2(50 BYTE)
);

COMMENT ON COLUMN T_CLIENT_2_SITE_TOKEN.SERIAL_NUMBER IS 'Seriennummer des Tokens';

COMMENT ON COLUMN T_CLIENT_2_SITE_TOKEN.BATTERIE_ENDE IS 'Ende der Batterielaufzeit';


ALTER TABLE T_CLIENT_2_SITE_TOKEN ADD (
  PRIMARY KEY
 (ID));

ALTER TABLE T_CLIENT_2_SITE_TOKEN ADD (
  CONSTRAINT FK_C2S_TOKEN_2_AUFTRAG
 FOREIGN KEY (AUFTRAG_ID)
 REFERENCES T_AUFTRAG (ID));

 CREATE SEQUENCE S_T_CLIENT_2_SITE_TOKEN_0 start with 1;
grant select on S_T_CLIENT_2_SITE_TOKEN_0 to public;

grant select on T_CLIENT_2_SITE_TOKEN to R_HURRICAN_READ_ONLY;
grant select, insert, update on T_CLIENT_2_SITE_TOKEN to R_HURRICAN_USER;
