--
-- Aufbau der neuen Tabellen fuer die IPSec Client-to-Site Tokens
--
drop table t_client_2_site_token;
drop table t_client_2_site;

drop sequence s_t_client_2_site_token_0;
drop sequence s_t_client_2_site_0;

CREATE TABLE T_IPSEC_C2S_TOKEN
(
  ID                   NUMBER(10)               NOT NULL,
  AUFTRAG_ID           NUMBER(10),
  SERIAL_NUMBER        VARCHAR2(50 BYTE),
  BEMERKUNG            VARCHAR2(250 BYTE),
  SAP_ORDER_ID         VARCHAR2(50 BYTE)        NOT NULL,
  LAUFZEIT_IN_MONATEN  NUMBER(10)               NOT NULL,
  LIEFERDATUM          DATE                     NOT NULL,
  BATTERIE_ENDE        DATE                     NOT NULL,
  RADIUS_ACCOUNT       VARCHAR2(50 BYTE),
  VPN_ID               NUMBER(10)               NOT NULL
);

COMMENT ON COLUMN T_IPSEC_C2S_TOKEN.SERIAL_NUMBER IS 'Seriennummer des Tokens';

COMMENT ON COLUMN T_IPSEC_C2S_TOKEN.BATTERIE_ENDE IS 'Ende der Batterielaufzeit';


ALTER TABLE T_IPSEC_C2S_TOKEN ADD (
  PRIMARY KEY
 (ID));

ALTER TABLE T_IPSEC_C2S_TOKEN ADD (
  CONSTRAINT FK_IPSEC_C2S_TOKEN_2_AUFTRAG
 FOREIGN KEY (AUFTRAG_ID)
 REFERENCES T_AUFTRAG (ID));

ALTER TABLE T_IPSEC_C2S_TOKEN
 ADD CONSTRAINT FK_IPSEC_C2S_TOKEN_2_VPN_ID
 FOREIGN KEY (VPN_ID)
 REFERENCES T_VPN (VPN_ID);

CREATE SEQUENCE S_T_IPSEC_C2S_TOKEN_0 start with 1;
grant select on S_T_IPSEC_C2S_TOKEN_0 to public;

create table T_IPSEC_C2S (
  ID                   NUMBER(10)               NOT NULL,
  AUFTRAG_ID           NUMBER(10)               NOT NULL,
  ADMIN_ZUGANG         CHAR(1)                  NOT NULL
);

COMMENT ON COLUMN T_IPSEC_C2S.ADMIN_ZUGANG IS 'Flag ob es sich um einen Admin-Zugang handelt';

CREATE SEQUENCE S_T_IPSEC_C2S_0 start with 1;
grant select on S_T_IPSEC_C2S_0 to public;

grant select on T_IPSEC_C2S_TOKEN to R_HURRICAN_READ_ONLY;
grant select, insert, update, delete on T_IPSEC_C2S_TOKEN to R_HURRICAN_USER;

grant select on T_IPSEC_C2S to R_HURRICAN_READ_ONLY;
grant select, insert, update on T_IPSEC_C2S to R_HURRICAN_USER;

