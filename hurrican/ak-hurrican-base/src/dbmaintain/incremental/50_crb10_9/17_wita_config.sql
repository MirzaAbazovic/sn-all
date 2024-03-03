--
-- Datenbanktabelle T_WITA_CONFIG
--
CREATE TABLE T_WITA_CONFIG
(
   ID                   NUMBER(19, 0) NOT NULL,
   CONFIG_KEY           VARCHAR2(50) NOT NULL,
   CONFIG_VALUE         VARCHAR2(100) NOT NULL,
   USERW                VARCHAR2(8) NOT NULL,
   DATEW                DATE NOT NULL
);

comment on column T_WITA_CONFIG.CONFIG_KEY is 'Schuessel des Konfigurationseintrags';
comment on column T_WITA_CONFIG.CONFIG_VALUE is 'Wert des Konfigurationseintrags';

--
-- Berechtigungen zum Lesen und Schreiben auf die Tabelle
--
GRANT SELECT, INSERT, UPDATE ON T_WITA_CONFIG TO R_HURRICAN_USER;
GRANT SELECT ON T_WITA_CONFIG TO R_HURRICAN_READ_ONLY;

--
-- Generator fuer den Primaerschluessel
--
CREATE SEQUENCE S_T_WITA_CONFIG_0
  START WITH 1
  MAXVALUE 999999999999999999999999999
  MINVALUE 1
  NOCYCLE
  CACHE 20
  NOORDER;

GRANT SELECT ON S_T_WITA_CONFIG_0 TO PUBLIC;

-- Trigger setzten fuer die Spalte DATEW
CREATE OR REPLACE TRIGGER TRBIU_WITA_CONFIG BEFORE INSERT OR UPDATE on T_WITA_CONFIG
for each row
begin
   -- DATEW setzen
   SELECT CURRENT_TIMESTAMP into :new.DATEW FROM dual;
end;
/
commit;
