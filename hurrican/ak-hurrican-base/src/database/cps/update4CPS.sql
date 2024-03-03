--
-- SQL-Script, um die notwendigen Anpassungen fuer die CPS-Integration
-- durchzufuehren
--

CREATE TABLE T_CPS_TX (
       ID NUMBER(10) NOT NULL
     , TAIFUN_ORDER__NO NUMBER(10)
     , AUFTRAG_ID NUMBER(10)
     , VERLAUF_ID NUMBER(10)
     , HW_RACK_ID NUMBER(10)
     , TX_STATE NUMBER(10) NOT NULL
     , TX_SOURCE NUMBER(10) NOT NULL
     , SERVICE_ORDER_TYPE NUMBER(10) NOT NULL
     , SERVICE_ORDER_PRIO NUMBER(10)
     , SO_STACK_ID NUMBER(10)
     , SO_STACK_SEQ NUMBER(10)
     , REGION NUMBER(10)
     , ESTIMATED_EXEC_TIME DATE NOT NULL
     , SERVICE_ORDER_DATA BLOB
     , REQUEST_AT DATE
     , REQUEST_DATA BLOB
     , RESPONSE_AT DATE
     , RESPONSE_DATA BLOB
     , TX_USER VARCHAR2(50)
     , USERW VARCHAR2(50) NOT NULL
     , TIMEST DATE NOT NULL
);

COMMENT ON TABLE T_CPS_TX IS 'Tabelle enthaelt die Informationen fuer CPS-Transaktionen.';

COMMENT ON COLUMN T_CPS_TX.TAIFUN_ORDER_NO
      IS 'ID des Taifun-Auftrags (notwendig, um nur eine Provisionierung pro Taifun-Auftrag zu realisieren)';
COMMENT ON COLUMN T_CPS_TX.AUFTRAG_ID 
      IS 'optional. ID des betroffenen technischen Auftrags';
COMMENT ON COLUMN T_CPS_TX.VERLAUF_ID 
      IS 'optional. ID des zugehoerigen Verlaufs';
COMMENT ON COLUMN T_CPS_TX.HW_RACK_ID 
      IS 'optional. ID des HW-Racks, fuer das die Tx ausgeloest wurde';
COMMENT ON COLUMN T_CPS_TX.TX_SOURCE 
      IS 'Angabe, woraus die TX erstellt wurde (z.B. Bauauftrag, DN-Leistungen, Profilaenderung, BSI, etc)';
COMMENT ON COLUMN T_CPS_TX.SERVICE_ORDER_TYPE 
      IS 'Art des Requests, z.B. create, modify, cancel, query';
COMMENT ON COLUMN T_CPS_TX.SERVICE_ORDER_PRIO 
      IS 'Prio fuer die Service-Order';
COMMENT ON COLUMN T_CPS_TX.SO_STACK_ID 
      IS 'Buendelung von mehreren ServiceOrders';
COMMENT ON COLUMN T_CPS_TX.SO_STACK_SEQ 
      IS 'Reihenfolge der ServiceOrders innerhalb einer Buendelung';
COMMENT ON COLUMN T_CPS_TX.ESTIMATED_EXEC_TIME 
      IS 'erwartetes Ausfuehrungsdatum';
COMMENT ON COLUMN T_CPS_TX.REQUEST_AT 
      IS 'Zeitstempel, wann der Request an den CPS gegeben wurde';
COMMENT ON COLUMN T_CPS_TX.RESPONSE_AT 
      IS 'Zeitstempel, wann die Antwort des CPS zu der TX kam';
COMMENT ON COLUMN T_CPS_TX.REQUEST_DATA 
      IS 'gesendete Request-Daten';
COMMENT ON COLUMN T_CPS_TX.RESPONSE_DATA 
      IS 'empfangene Answer Daten';
COMMENT ON COLUMN T_CPS_TX.TX_USER 
      IS 'Angabe des Users, der die TX ausgeloest hat (z.B. durch Profilaenderung)';
COMMENT ON COLUMN T_CPS_TX.SERVICE_ORDER_DATA 
      IS 'generierte XML-Struktur der ServiceOrderData - die zu provisionierenden Daten';      

ALTER TABLE T_CPS_TX ADD CONSTRAINT PK_T_CPS_TX PRIMARY KEY (ID);

CREATE INDEX IX_FK_CPSTX_2_AUFTRAG ON T_CPS_TX (AUFTRAG_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_CPS_TX ADD CONSTRAINT FK_CPSTX_2_AUFTRAG
  FOREIGN KEY (AUFTRAG_ID) REFERENCES t_auftrag (ID);

CREATE INDEX IX_FK_CPSTX_2_VERLAUF ON T_CPS_TX (VERLAUF_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_CPS_TX ADD CONSTRAINT FK_CPSTX_2_VERLAUF
  FOREIGN KEY (VERLAUF_ID) REFERENCES T_VERLAUF (ID);

CREATE INDEX IX_FK_CPSTX_2_HWRACK ON T_CPS_TX (HW_RACK_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_CPS_TX ADD CONSTRAINT FK_CPSTX_2_HWRACK
  FOREIGN KEY (HW_RACK_ID) REFERENCES T_HW_RACK (ID);
  
CREATE INDEX IX_FK_CPSTXSRC_2_REF ON T_CPS_TX (TX_SOURCE) TABLESPACE "I_HURRICAN";
ALTER TABLE T_CPS_TX ADD CONSTRAINT FK_CPSTXSRC_2_REF
  FOREIGN KEY (TX_SOURCE) REFERENCES t_reference (ID);

CREATE INDEX IX_FK_CPSTXSOTYPE_2_REF ON T_CPS_TX (SERVICE_ORDER_TYPE) TABLESPACE "I_HURRICAN";
ALTER TABLE T_CPS_TX ADD CONSTRAINT FK_CPSTXSOTYPE_2_REF
  FOREIGN KEY (SERVICE_ORDER_TYPE) REFERENCES t_reference (ID);

CREATE INDEX IX_FK_CPSTXSOPRIO_2_REF ON T_CPS_TX (SERVICE_ORDER_PRIO) TABLESPACE "I_HURRICAN";
ALTER TABLE T_CPS_TX ADD CONSTRAINT FK_CPSTXSOPRIO_2_REF
  FOREIGN KEY (SERVICE_ORDER_PRIO) REFERENCES t_reference (ID);

CREATE INDEX IX_FK_CPSTXSTATE_2_REF ON T_CPS_TX (TX_STATE) TABLESPACE "I_HURRICAN";
ALTER TABLE T_CPS_TX ADD CONSTRAINT FK_CPSTXSTATE_2_REF
  FOREIGN KEY (TX_STATE) REFERENCES t_reference (ID);

CREATE INDEX IX_FK_CPSTX_2_REGION ON T_CPS_TX (REGION) TABLESPACE "I_HURRICAN";
ALTER TABLE T_CPS_TX ADD CONSTRAINT FK_CPSTX_2_REGION
  FOREIGN KEY (REGION) REFERENCES t_niederlassung (ID);
commit;


CREATE TABLE T_CPS_TX_LOG (
       ID NUMBER(10) NOT NULL
     , CPS_TX_ID NUMBER(10) NOT NULL
     , MESSAGE VARCHAR2(4000)
     , TIMEST DATE
);

COMMENT ON TABLE T_CPS_TX_LOG IS 'Log-Tabelle fuer evtl. auftretende Probleme waehrend der Provisionierung';

ALTER TABLE T_CPS_TX_LOG ADD CONSTRAINT PK_T_CPS_TX_LOG PRIMARY KEY (ID);

CREATE INDEX IX_FK_CPSTXLOG_2_CPSTX ON T_CPS_TX_LOG (CPS_TX_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_CPS_TX_LOG ADD CONSTRAINT FK_CPSTXLOG_2_CPSTX
  FOREIGN KEY (CPS_TX_ID) REFERENCES T_CPS_TX (ID);
commit;



CREATE TABLE T_CPS_DATA_CHAIN_CONFIG (
       ID NUMBER(10) NOT NULL
     , PROD_ID NUMBER(10) NOT NULL
     , SO_TYPE_REF_ID NUMBER(10) NOT NULL
     , CHAIN_ID NUMBER(10) NOT NULL
);
COMMENT ON TABLE T_CPS_DATA_CHAIN_CONFIG 
  IS 'Tabelle definiert die ServiceChain fuer eine Kombination aus PROD_ID und SO_TYPE_REF_ID';

ALTER TABLE T_CPS_DATA_CHAIN_CONFIG
  ADD CONSTRAINT PK_T_CPS_DATA_CHAIN_CONFIG
      PRIMARY KEY (ID);

ALTER TABLE T_CPS_DATA_CHAIN_CONFIG
  ADD CONSTRAINT UQ_T_CPS_DATA_CHAIN_CONFIG
      UNIQUE (PROD_ID, SO_TYPE_REF_ID);

CREATE INDEX IX_FK_CPSDATACONFIG_2_PROD ON T_CPS_DATA_CHAIN_CONFIG (PROD_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_CPS_DATA_CHAIN_CONFIG
  ADD CONSTRAINT FK_CPSDATACONFIG_2_PROD
      FOREIGN KEY (PROD_ID)
      REFERENCES T_PRODUKT (PROD_ID);

CREATE INDEX IX_FK_CPSDATACONFIG_2_CHAIN ON T_CPS_DATA_CHAIN_CONFIG (CHAIN_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_CPS_DATA_CHAIN_CONFIG
  ADD CONSTRAINT FK_CPSDATACONFIG_2_CHAIN
      FOREIGN KEY (CHAIN_ID)
      REFERENCES t_service_chain (ID);

CREATE INDEX IX_FK_CPSDATACONFIG_2_REF ON T_CPS_DATA_CHAIN_CONFIG (SO_TYPE_REF_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_CPS_DATA_CHAIN_CONFIG
  ADD CONSTRAINT FK_CPSDATACONFIG_2_REF
      FOREIGN KEY (SO_TYPE_REF_ID)
      REFERENCES t_reference (ID);


-- Sequences
create SEQUENCE S_T_CPS_TX_0 start with 1;
grant select on S_T_CPS_TX_0 to public;

create SEQUENCE S_T_CPS_TX_STACK_SEQUENCE_0 start with 1;
grant select on S_T_CPS_TX_STACK_SEQUENCE_0 to public;

create SEQUENCE S_T_CPS_TX_LOG_0 start with 1;
grant select on S_T_CPS_TX_LOG_0 to public;

create SEQUENCE S_T_CPS_DATA_CHAIN_CONFIG_0 start with 1;
grant select on S_T_CPS_DATA_CHAIN_CONFIG_0 to public;


-- Triggers
drop trigger TRBIU_CPS_TX;
create trigger TRBIU_CPS_TX BEFORE INSERT or UPDATE on T_CPS_TX
for each row
begin
 -- TIMEST setzen
 SELECT CURRENT_TIMESTAMP into :new.TIMEST FROM dual;
end;
/
commit;

drop trigger TRBIU_CPS_TX_LOG;
create trigger TRBIU_CPS_TX_LOG BEFORE INSERT or UPDATE on T_CPS_TX_LOG
for each row
begin
 -- TIMEST setzen
 SELECT CURRENT_TIMESTAMP into :new.TIMEST FROM dual;
end;
/
commit;

drop trigger TRBIU_CPS_DATA_CHAIN_CONFIG;
create trigger TRBIU_CPS_DATA_CHAIN_CONFIG BEFORE INSERT or UPDATE on T_CPS_DATA_CHAIN_CONFIG
for each row
WHEN (new.ID is null)
begin
   -- ID und DATEW setzen
   select S_T_CPS_DATA_CHAIN_CONFIG_0.nextval into :new.ID from dual;
end;
/
commit;


--
-- Referenzdaten anlegen
--

-- Referenz-Werte fuer 'ServiceOrderTypes' anlegen
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14000, 'CPS_SERVICE_ORDER_TYPE', 'createSubscriber', '1', 10, 
	'Wert definiert einen ServiceOrder-Typ, um einen Auftrag durch den CPS neu anzulegen');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14001, 'CPS_SERVICE_ORDER_TYPE', 'modifySubscriber', '1', 20, 
	'Wert definiert einen ServiceOrder-Typ, um einen bestehenden Auftrag durch den CPS zu aendern');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14002, 'CPS_SERVICE_ORDER_TYPE', 'cancelSubscriber', '1', 30, 
	'Wert definiert einen ServiceOrder-Typ, um einen bestehenden Auftrag durch den CPS zu loeschen');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14003, 'CPS_SERVICE_ORDER_TYPE', 'getSOStatus', '1', 40, 
	'Wert definiert einen ServiceOrder-Typ, um Statusmeldung abzufragen');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14004, 'CPS_SERVICE_ORDER_TYPE', 'cancelTransaction', '0', null, 
	'Wert definiert den ServiceOrder-Typ, ueber den eine CPS-Transaction storniert werden kann');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14005, 'CPS_SERVICE_ORDER_TYPE', 'initializeMDU', '0', null, 
	'Wert definiert den ServiceOrder-Typ, ueber den eine MDU zu initialisieren');
commit;

	
-- Referenz-Werte fuer 'TX_SOURCE' (definiert die Herkunft der CPS-Transaktion)
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14100, 'CPS_TX_SOURCE', 'HURRICAN_VERLAUF', '1', 10, 
	'Wert definiert, dass die Transaction durch einen Hurrican-Bauauftrag erstellt wurde');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14101, 'CPS_TX_SOURCE', 'HURRICAN_DN', '1', 20, 
	'Wert definiert, dass die Transaction durch eine Rufnummernleistungsaenderung erstellt wurde');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14102, 'CPS_TX_SOURCE', 'HURRICAN_LOCK', '1', 30, 
	'Wert definiert, dass die Transaction durch eine (Ent-)Sperre erstellt wurde');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14103, 'CPS_TX_SOURCE', 'HURRICAN_ORDER', '1', 40, 
	'Wert definiert, dass die Transaction auf einem Auftrag explizit ausgeloest wurde');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14104, 'CPS_TX_SOURCE', 'HURRICAN_MDU', '0', null, 
	'Wert definiert, dass die Transaction fuer eine MDU bestimmt ist');
	
-- Referenz-Werte fuer den CPS-Transaktionsstatus
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14200, 'CPS_TX_STATE', 'PREPARING', '1', 10, 
	'Wert definiert, dass die TX-Daten vorbereitet/gesammelt werden - noch keine Uebertragung an CPS');	
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14205, 'CPS_TX_STATE', 'PREPARING_FAILURE', '1', 10, 
	'Wert definiert, dass bei der Datenermittlung ein Fehler aufgetreten ist');	
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14210, 'CPS_TX_STATE', 'IN_PROVISIONING', '1', 20, 
	'Wert definiert, dass sich die TX noch in der Provisionierung (beim CPS) befindet');	
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14220, 'CPS_TX_STATE', 'CANCELLED', '1', 30, 
	'Wert definiert, dass die TX vor der Provisionierung abgebrochen wurde.');	
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14230, 'CPS_TX_STATE', 'TRANSMISSION_FAILURE', '1', 40, 
	'Wert definiert, dass die TX nicht korrekt an den CPS uebergeben werden konnte.');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14240, 'CPS_TX_STATE', 'FAILURE', '1', 50, 
	'Wert definiert, dass die TX vom CPS fehlerhaft oder ueberhaupt nicht provisioniert wurde');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14250, 'CPS_TX_STATE', 'FAILURE_CLOSED', '1', 60, 
	'Wert definiert, dass die TX vom CPS fehlerhaft gemeldet wurde. Die Tx wurde aber manuell geschlossen.');		
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14260, 'CPS_TX_STATE', 'SUCCESS', '1', 70, 
	'Wert definiert, dass die TX vom CPS erfolgreich abgeschlossen wurde');		
commit;


-- ServiceOrder-Prios definieren
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14300, 'CPS_SERVICE_ORDER_PRIO', 'HIGH', 1, '1', 70, 
	'Wert definiert eine hohe Prioritaet fuer eine CPS-Transaction. (INT-Wert = Value fuer CPS)');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14310, 'CPS_SERVICE_ORDER_PRIO', 'DEFAULT', 5, '1', 70, 
	'Wert definiert die Standard-Prioritaet fuer eine CPS-Transaction. (INT-Wert = Value fuer CPS)');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (14320, 'CPS_SERVICE_ORDER_PRIO', 'LOW', 10, '1', 70, 
	'Wert definiert eine niedrige Prioritaet fuer eine CPS-Transaction. (INT-Wert = Value fuer CPS)');
commit;


-- Produkt-Konfiguration erweitern 
alter table T_PRODUKT add CPS_PROVISIONING CHAR(1);
COMMENT ON COLUMN T_PRODUKT.CPS_PROVISIONING 
  IS 'Flag definiert, ob das Produkt ueber den CPS provisioniert werden kann';
alter table T_PRODUKT add CPS_PROD_NAME VARCHAR2(30);
COMMENT ON COLUMN T_PRODUKT.CPS_PROD_NAME
  IS 'Produkt-Name fuer den CPS';
commit;

-- weitere Verlauf-Stati fuer Abteilungs-Verlaeufe anlegen
insert into T_VERLAUF_STATUS (ID, VERLAUF_STATUS) values (5, 'CPS-Provisionierung');
insert into T_VERLAUF_STATUS (ID, VERLAUF_STATUS) values (6, 'erledigt CPS');
commit;

-- Auftrag mit Flag fuer 'manuelle Provisionierung' markieren (CPS-Prov. verhindern)
alter table T_AUFTRAG_TECHNIK add PREVENT_CPS_PROV CHAR(1);
COMMENT ON COLUMN T_AUFTRAG_TECHNIK.PREVENT_CPS_PROV 
  IS 'Flag definiert, dass dieser Auftrag nicht ueber den CPS provisioniert werden darf (wenn auf 1 gesetzt)';
commit;


-- Verlauf mit Flag fuer 'manuelle Provisionierung' markieren (CPS-Prov. verhindern)
alter table T_VERLAUF add PREVENT_CPS_PROV CHAR(1);
COMMENT ON COLUMN T_VERLAUF.PREVENT_CPS_PROV 
  IS 'Flag definiert, dass dieser Verlauf nicht ueber den CPS provisioniert werden darf (wenn auf 1 gesetzt)';
commit;

-- BA-Anlass um CPS-ServiceOrderType erweitern. Notwendig???
alter table T_BA_VERL_ANLASS add CPS_SO_TYPE NUMBER(10);
COMMENT ON COLUMN T_BA_VERL_ANLASS.CPS_SO_TYPE
  IS 'Definition des zugehoerigen ServiceOrder-Types des CPS';
CREATE INDEX IX_FK_BAVERLANL_2_REF ON T_BA_VERL_ANLASS (CPS_SO_TYPE) TABLESPACE "I_HURRICAN";
ALTER TABLE T_BA_VERL_ANLASS ADD CONSTRAINT FK_BAVERLANL_2_REF
  FOREIGN KEY (CPS_SO_TYPE) REFERENCES t_reference (ID);
update T_BA_VERL_ANLASS set CPS_SO_TYPE=14000 where ID in (27,28);
update T_BA_VERL_ANLASS set CPS_SO_TYPE=14002 where ID in (13);
update T_BA_VERL_ANLASS set CPS_SO_TYPE=14001 where CPS_SO_TYPE is null;
update T_BA_VERL_ANLASS set CPS_SO_TYPE=null where ID in (70,55);
commit;



-- DN-Leistungen um CPS-Tx ID erweitern (fuer create und cancel)
-- (kein ForeignKey verwenden - wg. Transaction-Handling)
alter table T_LEISTUNG_DN add CPS_TX_ID_CREATION number(10);
CREATE INDEX IX_LSTDN_2_CPSTX ON T_LEISTUNG_DN (CPS_TX_ID_CREATION) TABLESPACE "I_HURRICAN";
comment on column T_LEISTUNG_DN.CPS_TX_ID_CREATION is 
  'Referenz auf die CPS-Tx, ueber die die Rufnummernleistung angelegt wurde. (Keine FK-Referenzierung!)';

alter table T_LEISTUNG_DN add CPS_TX_ID_CANCEL number(10);
CREATE INDEX IX_LSTDN_2_CPSTX2 ON T_LEISTUNG_DN (CPS_TX_ID_CANCEL) TABLESPACE "I_HURRICAN";
comment on column T_LEISTUNG_DN.CPS_TX_ID_CANCEL is 
  'Referenz auf die CPS-Tx, ueber die die Rufnummernleistung gekuendigt wurde. (Keine FK-Referenzierung!)';  
commit;


-- TODO: DSLAM-Typ einfuehren
-- TODO: Port-Identifier (EQN) herstellerabhaengig definierbar (per RegularExpression)



-- BEACHTEN: Verlauf NICHT um CPS-Tx ID erweitern! Verlauf-ID ist schon in T_CPS_TX...
--    TODO: Verlauf als 'automatische Provisionierung' markieren (nur in GUI - ueber VerlaufAbteilung.Status)
--    TODO: bei Verlaufs-Verteilung (Dispo) Flag PREVENT_CPS_PROV anbieten



-- Eintraege fuer AK-AUTHENTICATION
alter session set nls_date_format='yyyy-mm-dd';
insert into APPLICATION (ID, NAME, DESCRIPTION) values (4, 'hurrican.web', 'WebApp von Hurrican');
insert into ACCOUNT (ID, DB_ID, APP_ID, ACC_NAME, ACCOUNTUSER, ACCOUNTPASSWORD, MAX_ACTIVE, MAX_IDLE) 
	values (27, 3, 4, 'hurrican.web.billing.default', 'hurrican', 'T+95oGvGFTzWJVt4V+Bw1g==', 3 ,1);  
insert into ACCOUNT (ID, DB_ID, APP_ID, ACC_NAME, ACCOUNTUSER, ACCOUNTPASSWORD, MAX_ACTIVE, MAX_IDLE) 
	values (28, 4, 4, 'hurrican.web.cc.writer', 'HURRICANWRITER', 'dyMRupr3P1Nl1OkoxnPWiQ==', 3 ,1);
insert into USERS (ID, LOGINNAME, NAME, FIRSTNAME, DEP_ID, PASSWORD, ACTIVE)
    values (xxx, 'hurrican-ws', 'ITS', 'WebApp', 10, '2200-01-01', '1');
insert into USERACCOUNT (ID, USER_ID, ACCOUNT_ID, DB_ID) values (yyy, xxx, 27, 3);
insert into USERACCOUNT (ID, USER_ID, ACCOUNT_ID, DB_ID) values (yyy, xxx, 28, 4);
insert into ROLE (ID, NAME, DESCRIPTION, APP_ID, IS_ADMIN) values (200, 'AKHURRICAN_WEB', 'Rolle fuer den WebApp User', 4, 0);
insert into USERROLE (ID, USER_ID, ROLE_ID) values (yyy, xxx, 200);

