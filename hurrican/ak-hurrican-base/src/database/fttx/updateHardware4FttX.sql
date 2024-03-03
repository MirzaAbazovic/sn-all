--
-- Update-Script
-- Erweitert die HW-Tabellen fuer die FttX-Komponenten
--

CREATE TABLE T_HW_RACK_MDU (
       RACK_ID NUMBER(10) NOT NULL
     , OLT_RACK_ID NUMBER(10) NOT NULL
     , SERIAL_NO VARCHAR2(50) NOT NULL
     , IP_ADDRESS VARCHAR2(20)
     , CATV_ONLINE CHAR(1)
     , OLT_FRAME VARCHAR2(2) NOT NULL
     , OLT_SHELF VARCHAR2(2) NOT NULL
     , OLT_GPON_PORT VARCHAR2(2) NOT NULL
     , OLT_GPON_ID VARCHAR2(2) NOT NULL
);
COMMENT ON COLUMN T_HW_RACK_MDU.OLT_FRAME IS 'Angabe des OLT-Frames an dem die MDU angeschlossen ist';
COMMENT ON COLUMN T_HW_RACK_MDU.OLT_SHELF IS 'Angabe des OLT-Shelfs an dem die MDU angeschlossen ist';
COMMENT ON COLUMN T_HW_RACK_MDU.OLT_GPON_PORT IS 'Angabe des OLT G.PON Ports an dem die MDU angeschlossen ist';

ALTER TABLE T_HW_RACK_MDU
  ADD CONSTRAINT PK_T_HW_RACK_MDU
      PRIMARY KEY (RACK_ID);

CREATE INDEX IX_FK_HWRACKMDU_2_HWRACK ON T_HW_RACK_MDU (RACK_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_HW_RACK_MDU
  ADD CONSTRAINT FK_HWRACKMDU_2_HWRACK
      FOREIGN KEY (RACK_ID)
      REFERENCES T_HW_RACK (ID);
      

CREATE TABLE T_HW_RACK_OLT (
       RACK_ID NUMBER(10) NOT NULL
     , SERIAL_NO VARCHAR2(50)
);

ALTER TABLE T_HW_RACK_OLT
  ADD CONSTRAINT PK_T_HW_RACK_OLT
      PRIMARY KEY (RACK_ID);

CREATE INDEX IX_FK_HWRACKOLT_2_HWRACK ON T_HW_RACK_OLT (RACK_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_HW_RACK_OLT
  ADD CONSTRAINT FK_HWRACKOLT_2_HWRACK
      FOREIGN KEY (RACK_ID)
      REFERENCES T_HW_RACK (ID);

commit;


GRANT SELECT ON T_HW_RACK_MDU TO R_HURRICAN_READ_ONLY;
GRANT SELECT ON T_HW_RACK_OLT TO R_HURRICAN_READ_ONLY;
GRANT SELECT, INSERT, UPDATE ON T_HW_RACK_MDU TO R_HURRICAN_READ_ONLY;
GRANT SELECT, INSERT, UPDATE ON T_HW_RACK_OLT TO R_HURRICAN_READ_ONLY;
GRANT SELECT, INSERT, UPDATE, DELETE ON T_HW_RACK_MDU TO R_HURRICAN_HW_ADMIN;
GRANT SELECT, INSERT, UPDATE, DELETE ON T_HW_RACK_OLT TO R_HURRICAN_HW_ADMIN;
commit;


-- Constraint fuer erlaubte HW-Typs erweitern
ALTER TABLE T_HW_RACK DROP CONSTRAINT CK_HW_RACK_TYP;
ALTER TABLE T_HW_RACK
  add CONSTRAINT CK_HW_RACK_TYP
     CHECK (RACK_TYP IN ('DSLAM', 'DLU', 'ROUTER', 'LTG', 'UEVT', 'MDU', 'OLT'));
ALTER TABLE T_HW_RACK ENABLE CONSTRAINT CK_HW_RACK_TYP;
commit;

insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (9505, 'HW_RACK_TYPE', 'OLT', 1, 60, '');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (9506, 'HW_RACK_TYPE', 'MDU', 1, 70, '');
commit;


-- HVT-Gruppe um Cluster-ID (Referenz auf HVT_GRUPPE.ID) erweitern
alter table T_HVT_GRUPPE add CLUSTER_ID NUMBER(10);
CREATE INDEX IX_FK_HVTGCLUSTER_2_HVTG ON T_HVT_GRUPPE (CLUSTER_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_HVT_GRUPPE
  ADD CONSTRAINT FK_HVTGCLUSTER_2_HVTG
      FOREIGN KEY (CLUSTER_ID)
      REFERENCES T_HVT_GRUPPE (HVT_GRUPPE_ID);
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (11008, 'STANDORT_TYP', 'FTTX_FC', 1, null, 'FC-Raum (=Cluster) fuer FttX-Standorte');
commit;




-- 
-- TODO in HCPROD
--


-- neuen Baugruppen-Typ anlegen
insert into T_HW_BAUGRUPPEN_TYP (ID, NAME, PORT_COUNT, DESCRIPTION, IS_ACTIVE) 
	values (37, 'MDU', 8, 'MDU mit 8 VDSL Ports, 4 POTS Ports', '1');
	
drop sequence S_T_HW_BAUGRUPPEN_TYP_0;
create sequence S_T_HW_BAUGRUPPEN_TYP_0 start with 38;
grant select on S_T_HW_BAUGRUPPEN_TYP_0 to public;

-- neue Physiktypen definieren
insert into T_PHYSIKTYP (ID, NAME, HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP)
  values (800, 'FTTB_VDSL', 2, 100000, 'VDSL', 4);
insert into T_PHYSIKTYP (ID, NAME, HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP)
  values (801, 'FTTB_POTS', 2, NULL, 'POTS', 4);
insert into T_PHYSIKTYP (ID, NAME, HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP, BESCHREIBUNG)
  values (802, 'FTTB_INT', 2, NULL, NULL, 4,
  'Physiktyp fuer Verbindungen im FttX-Bereich zwischen der Whg. und DTAG Leiste');
insert into T_PHYSIKTYP (ID, NAME, HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP)
  values (803, 'FTTH', 2, 100000, 'FTTH', 4);
commit;


-- DTAG Referenz erstellen (wg. Rangierungs-Tool)
insert into T_PRODUKT_DTAG (ID, RANG_SS_TYPE) values (7, 'FTTB');
insert into T_PRODUKT_DTAG (ID, RANG_SS_TYPE) values (8, 'FTTH');
commit;


-- Referenzen fuer Rangierungs-Status anlegen
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (15000, 'RANGIERUNG_STATUS', 'getrennt', 1, 10, 
  'Status zeigt an, dass die (FttX)Rangierung aktuell getrennt ist.');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (15001, 'RANGIERUNG_STATUS', 'verbunden', 1, 20, 
  'Status zeigt an, dass die (FttX)Rangierung aktuell verbunden ist.');

-- Rangierung um Status-Referenz erweitern
alter table T_RANGIERUNG add RANG_STATUS_REF_ID NUMBER(10);
CREATE INDEX IX_FK_RANG_2_REF ON T_RANGIERUNG (RANG_STATUS_REF_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_RANGIERUNG
  ADD CONSTRAINT FK_RANG_2_REF
      FOREIGN KEY (RANG_STATUS_REF_ID)
      REFERENCES T_REFERENCE (ID);
commit;


insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (14600, 'ANSCHLUSSDOSE', 'TAE', 1, 10, 'Anschlussdosen fuer FTTX');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (14601, 'ANSCHLUSSDOSE', 'UAE', 1, 20, 'Anschlussdosen fuer FTTX');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (14602, 'ANSCHLUSSDOSE', 'VDO', 1, 30, 'Anschlussdosen fuer FTTX');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (14603, 'ANSCHLUSSDOSE', 'OAD SC Kupplung', 1, 40, 'Anschlussdosen fuer FTTX');
  
alter table T_NIEDERLASSUNG
	ADD AREA_NO NUMBER(10);
	
UPDATE T_NIEDERLASSUNG SET AREA_NO = 1 WHERE ID = 1;
UPDATE T_NIEDERLASSUNG SET AREA_NO = 4 WHERE ID = 2;
UPDATE T_NIEDERLASSUNG SET AREA_NO = 2 WHERE ID = 3;
UPDATE T_NIEDERLASSUNG SET AREA_NO = 3 WHERE ID = 4;


-- HVT-View
CREATE or REPLACE FORCE VIEW VH_H2T_SERVICE_ROOM
  AS SELECT 
    hg.ONKZ as ONKZ,
    hs.ASB as ASB,
    hs.KVZ_NUMMER as KVZ_NUMMER,
    hs.KVZ_SCHALTNUMMER as KVZ_SCHALTNUMMER,
    hg.ORTSTEIL as SERVICE_ROOM,
    hg.STRASSE as STREET,
    hg.HAUS_NR as HOUSE_NUM,
    hg.PLZ as POSTAL_CODE,
    hg.ORT as CITY,
    ru.STR_VALUE as SERVICE_ROOM_TYPE,
    hg.DSL_ANALOG_TERMIN as DSL_ANALOG_TERMIN,
    hg.DSL_ISDN_TERMIN as DSL_ISDN_TERMIN,
    hg.DSL2P_ANALOG_TERMIN as DSL2P_ANALOG_TERMIN,
    hg.DSL2P_ISDN_TERMIN as DSL2P_ISDN_TERMIN,
    hg.SDSL_TERMIN as SDSL_TERMIN,
    hg.CONNECT_TERMIN as CONNECT_TERMIN,
    hg.DSL2P_ONLY_TERMIN as DSL2P_ONLY_TERMIN,
    hg.VOIP_TERMIN as VOIP_TERMIN,
    nl.AREA_NO as AREA_NO
  FROM
    T_HVT_GRUPPE hg
    inner join T_HVT_STANDORT hs on hs.HVT_GRUPPE_ID=hg.HVT_GRUPPE_ID
    left join T_REFERENCE r on hs.STANDORT_TYP_REF_ID=r.ID
    left join T_REFERENCE ru on r.UNIT_ID=ru.ID
    left join T_NIEDERLASSUNG nl on hg.NIEDERLASSUNG_ID = nl.ID 
  WHERE
    hs.ASB is not null and ru.STR_VALUE is not null and
    hs.GUELTIG_BIS > SYSDATE
;


-- Union mit KuP erstellen
CREATE OR REPLACE FORCE VIEW v_h2t_service_room (
	onkz,
    asb,
    kvz_nummer,
    kvz_schaltnummer,
    service_room,
    street,
    house_num,
    postal_code,
    city,
    service_room_type,
    dsl_analog_termin,
    dsl_isdn_termin,
    dsl2p_analog_termin,
    dsl2p_isdn_termin,
    sdsl_termin,
    connect_termin,
    dsl2p_only_termin,
    voip_termin,
    area_no
    )
AS
   SELECT a.onkz, a.asb, a.kvz_nummer, a.kvz_schaltnummer, a.service_room,
          a.street, a.house_num, a.postal_code, a.city, a.service_room_type,
          a.dsl_analog_termin, a.dsl_isdn_termin, a.dsl2p_analog_termin,
          a.dsl2p_isdn_termin, a.sdsl_termin, a.connect_termin, 
          a.dsl2p_only_termin, a.voip_termin, a.area_no
     FROM vh_h2t_service_room a
   UNION
   SELECT a.onkz, a.asb, a.kvz_nummer, a.kvz_schaltnummer, a.service_room,
          a.street, a.house_num, a.postal_code, a.city, a.service_room_type,
          a.dsl_analog_termin, a.dsl_isdn_termin, a.dsl2p_analog_termin,
          a.dsl2p_isdn_termin, a.sdsl_termin, a.connect_termin, 
          a.dsl2p_only_termin, a.voip_termin, a.area_no
     FROM (SELECT bb.onkz || '_' || bb.asb || '_' || bb.kvz_nummer AS comp
             FROM vh_h2t_service_room bb) b,
          vkup_h2t_service_room@kupvis a
    WHERE a.asb<>0 AND a.comp = b.comp(+) AND b.comp IS NULL;

