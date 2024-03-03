--
-- SQL-Script fuer die SDSL-Bandbreiten 6800 und 10000
--

INSERT INTO T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, LEITUNGSART, PROD_NAME_PATTERN, AKTIONS_ID,
	BRAUCHT_DN, AUFTRAGSERSTELLUNG, LTGNR_VORS, LTGNR_ANLEGEN, BRAUCHT_BUENDEL, 
	ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, ACCOUNT_VORS, LI_NR, VPN_PHYSIK, IS_PARENT, PROJEKTIERUNG,
	CHECK_CHILD, VERTEILUNG_DURCH, EXPORT_KDP_M, AUTO_PRODUCT_CHANGE, IS_COMBI_PRODUKT, GUELTIG_VON, GUELTIG_BIS) 
	VALUES (450, 4, '', 'SDSL', null, 'SDSL {DOWNSTREAM}', 1, 
	0, 0, 'DV', 1, 1, 
	1, 0, 1, 'as', 4, 1, 1, 1,
	0, 4, 1, 0, 0, '2007-03-01', '2200-01-01');
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (450, 1),(450,2);
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (450, 34),(450, 35);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (450,2);

-- Endgeraete-Konfig
INSERT INTO T_EG (ID, INTERNE__ID, NAME, BESCHREIBUNG, typ, EXT_LEISTUNG__NO, VERFUEGBAR_VON, VERFUEGBAR_BIS,
	GARANTIEZEIT, PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING, CONF_S0BACKUP)
	VALUES (28, 28, 'SHDSL Router', 'Router fuer SHDSL', 4, null, '2007-03-01', '2200-01-01',
	null, null, 0, 0, 0);
INSERT INTO T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) VALUES (450, 28, 0);
INSERT INTO T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) VALUES (450, 12, 0);

-- neue Down/Upstream-Leistungen anlegen
insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE, STR_VALUE,
	DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS) 
	values (19, '256 kbit/s', 10021, 'DOWNSTREAM', 256, '256',
	false, false, true, false, false, true, '2007-02-01', '2200-01-01');
insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE, STR_VALUE,
	DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS) 
	values (20, '512 kbit/s', 10022, 'DOWNSTREAM', 512, '512',
	false, false, true, false, false, true, '2007-02-01', '2200-01-01');
insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE, STR_VALUE,
	DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS) 
	values (21, '2300 kbit/s', 10023, 'DOWNSTREAM', 2300, '2300',
	false, false, true, false, false, true, '2007-02-01', '2200-01-01');
insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE, STR_VALUE,
	DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS) 
	values (22, '4600 kbit/s', 10024, 'DOWNSTREAM', 4600, '4600',
	false, false, true, false, false, true, '2007-02-01', '2200-01-01');
insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE, STR_VALUE,
	DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS) 
	values (23, '6800 kbit/s', 10025, 'DOWNSTREAM', 6800, '6800',
	false, false, true, false, false, true, '2007-02-01', '2200-01-01');	
insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE, STR_VALUE,
	DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS) 
	values (24, '10000 kbit/s', 10026, 'DOWNSTREAM', 10000, '10000',
	false, false, true, false, false, true, '2007-02-01', '2200-01-01');

-- Zuordnung techn. Leistungen zu SDSL-Produkt
-- Downstreams
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (450, 19, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (450, 20, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (450, 21, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (450, 22, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (450, 23, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (450, 24, null, 0);
-- ISDN-Backup
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (450, 17, null, 0);

-- Physikzuordnung
UPDATE T_PHYSIKTYP set MAX_BANDWIDTH=4600 where ID in (9,509);
INSERT INTO T_PHYSIKTYP (ID, NAME, BESCHREIBUNG, HVT_TECHNIK_ID, MAX_BANDWIDTH) values (515, 'SHDSL-DA (H)', null, 2, 10000);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (450,9),(450,509),(450,515);

-- TODO Mapping fuer Produkt aufnehmen, wenn in Taifun eingerichtet
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE) VALUES (???, ???, 450, 'sdsl');


-- TODO Taifun-Leistungen konfigurieren (Produktmapping und techn. Leistungen)







