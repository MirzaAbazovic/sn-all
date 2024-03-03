--
-- Datenbanktabelle T_IP_ADDRESS
--
CREATE TABLE T_IP_ADDRESS
(
   ID                   NUMBER (19, 0) NOT NULL,
   ADDRESS_TYPE         VARCHAR2 (50) NOT NULL,
   ADDRESS              VARCHAR2 (50) NOT NULL,
   GUELTIG_VON          DATE NOT NULL,
   GUELTIG_BIS          DATE NOT NULL,
   USERW                VARCHAR2(8) NOT NULL,
   DATEW                DATE NOT NULL,
   TAIFUN_ORDER_NO      NUMBER(19) NOT NULL,
   NET_ID               NUMBER(19) NOT NULL
);

--
-- Primaerschluessel hinzufuegen
--
ALTER TABLE T_IP_ADDRESS ADD (
  CONSTRAINT T_IP_ADDRESS_PK
  PRIMARY KEY
  (ID));

comment on column T_IP_ADDRESS.ADDRESS_TYPE is 'Typ der IP-Adresse z.B. IPv4 oder IPv6.';
comment on column T_IP_ADDRESS.ADDRESS is 'Eigentliche Adresse z.B. 192.168.1.200 oder 2001:DB8:A001::/48';
comment on column T_IP_ADDRESS.GUELTIG_VON is 'Ab wann die Adresse gueltig ist.';
comment on column T_IP_ADDRESS.GUELTIG_BIS is 'Bis wann die Adresse gueltig ist.';
comment on column T_IP_ADDRESS.USERW is 'Wer als letzter den aktuellen Eintrag bearbeitet hat.';
comment on column T_IP_ADDRESS.DATEW is 'Wann das letzte Mal der aktuelle Eintrag bearbeitet wurde.';
comment on column T_IP_ADDRESS.TAIFUN_ORDER_NO is 'Die Taifun-Auftragsnummer.';
comment on column T_IP_ADDRESS.NET_ID is 'ID des Netzblocks welcher von MOnline bezogen wurde und zu dem der aktuelle Eintrag gehoert.';

--
-- Spalte fuer Fremdschluessel hinzufuegen
--
ALTER TABLE T_IP_ADDRESS
 ADD (PREFIX  NUMBER(19));

--
-- Fremdschluessel hinzufuegen
--
ALTER TABLE T_IP_ADDRESS ADD CONSTRAINT FK_IP_ADDRESS_PREFIX
  FOREIGN KEY (PREFIX)
  REFERENCES T_IP_ADDRESS (ID);



--
-- Berechtigungen zum Lesen und Schreiben auf die Tabelle
--
GRANT SELECT, INSERT, UPDATE ON T_IP_ADDRESS TO R_HURRICAN_USER;
GRANT SELECT ON T_IP_ADDRESS TO R_HURRICAN_READ_ONLY;

--
-- Generator fuer den Primaerschluessel
--
CREATE SEQUENCE S_T_IP_ADDRESS_0
  START WITH 1
  MAXVALUE 999999999999999999999999999
  MINVALUE 1
  NOCYCLE
  CACHE 20
  NOORDER;

GRANT SELECT ON S_T_IP_ADDRESS_0 TO PUBLIC;

-- Trigger setzten fuer die Spalte DATEW
CREATE OR REPLACE TRIGGER TRBIU_IP_ADDRESS BEFORE INSERT OR UPDATE on T_IP_ADDRESS
for each row
begin
   -- DATEW setzen
   SELECT CURRENT_TIMESTAMP into :new.DATEW FROM dual;
end;
/
commit;
