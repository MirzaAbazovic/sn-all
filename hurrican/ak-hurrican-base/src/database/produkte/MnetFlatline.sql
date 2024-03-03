--
-- SQL-Statements fuer die Einrichtung von Produkt 'M-net flatline 128k'
--

--INSERT INTO T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, LEITUNGSART, UPSTREAM, AKTIONS_ID,
--	BRAUCHT_DN, BRAUCHT_DN4ACCOUNT, AUFTRAGSERSTELLUNG, LTGNR_VORS, LTGNR_ANLEGEN, BRAUCHT_BUENDEL, PROD_ID_TEXT,
--	ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, ACCOUNT_VORS, LI_NR, VPN_PHYSIK, IS_PARENT,
--	CHECK_CHILD, VERTEILUNG_DURCH, EXPORT_KDP_M, GUELTIG_VON, GUELTIG_BIS) 
--	VALUES (70, 5, null, 'M-net flatline 128k', null, null, 1, 
--	0, 0, 0, null, 0, 0, '70', 
--	1, 0, 0, 'as', 1, 0, 0, 
--	0, null, 1, '2006-05-01', '2200-01-01');

--INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (70, 1);
--INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (70,2);
--INSERT INTO T_BA_VERL_NEU (PROD_ID, EWSD, SDH, SCT, IPS, AUSBLENDEN) VALUES (70,0,0,0,1,0);

--
-- Konfiguration der Einwahlrufnummer 
-- (unterschiedlich zwischen 'AK-online flat24' und 'M-net flatline 128k'
--
-- ausgefuehrt am 22.05.2006 09:30
--ALTER TABLE T_REFERENCE ADD COLUMN DESCRIPTION VARCHAR(255);
--INSERT INTO T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, DESCRIPTION) 
--	VALUES (7000, 'EINWAHL_DN_4_PRODUKT', '0 19 38 25 24', 14, false, 
--	'Definition der Einwahlrufnummer fuer das Produkt <AK-online flat24> (ID: 14) - IntValue=ProduktId; StrValue=Einwahlrufnummer');

INSERT INTO T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, DESCRIPTION) 
	VALUES (7001, 'EINWAHL_DN_4_PRODUKT', '0 19 30 57', 322, false, 
	'Definition der Einwahlrufnummer fuer die Leistung <M-net flatline 128k> (auf Produkt 322) - IntValue=ProduktId; StrValue=Einwahlrufnummer');
INSERT INTO T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, DESCRIPTION) 
	VALUES (7002, 'EINWAHL_DN_4_PRODUKT', '0 19 30 57', 323, false, 
	'Definition der Einwahlrufnummer fuer die Leistung <M-net flatline 128k> (auf Produkt 323) - IntValue=ProduktId; StrValue=Einwahlrufnummer');
INSERT INTO T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, DESCRIPTION) 
	VALUES (7003, 'EINWAHL_DN_4_PRODUKT', '0 19 30 57', 336, false, 
	'Definition der Einwahlrufnummer fuer die Leistung <M-net flatline 128k> (auf Produkt 336) - IntValue=ProduktId; StrValue=Einwahlrufnummer');
INSERT INTO T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, DESCRIPTION) 
	VALUES (7004, 'EINWAHL_DN_4_PRODUKT', '0 19 30 57', 337, false, 
	'Definition der Einwahlrufnummer fuer die Leistung <M-net flatline 128k> (auf Produkt 337) - IntValue=ProduktId; StrValue=Einwahlrufnummer');
INSERT INTO T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, DESCRIPTION) 
	VALUES (7005, 'EINWAHL_DN_4_PRODUKT', '0 19 30 57', 338, false, 
	'Definition der Einwahlrufnummer fuer die Leistung <M-net flatline 128k> (auf Produkt 338) - IntValue=ProduktId; StrValue=Einwahlrufnummer');

-- Leistung konfigurieren
ALTER TABLE T_BILLING_LEISTUNG_KONFIG ADD COLUMN PARAMETER VARCHAR(50) AFTER IPS;
INSERT INTO T_BILLING_LEISTUNG_KONFIG (LEISTUNG_BEZ_BILLING, NAME, IS_DEFAULT, IPS, EWSD, SDH, SCT, PARAMETER, DESCRIPTION, GUELTIG_VON, GUELTIG_BIS)
	VALUES ('10007', 'Einwahlaccount', 0, 1, 0, 0, 0, 'as', 'Leistung, um einen Einwahlaccount anzulegen.', '2006-05-01', '2200-01-01');

-- Verlaufsaenderungen konfigurieren
INSERT INTO T_BA_VERL_AEND (ID, TEXT, IPS, AKT, BA_VERL_AEND_GRUPPE_ID, CONFIGURABLE, IS_AUFTRAGSART)
	VALUES (63, 'Zugang Einwahlaccount', 1, 1, 2, 1, 0);
INSERT INTO T_BA_VERL_AEND (ID, TEXT, IPS, AKT, BA_VERL_AEND_GRUPPE_ID, CONFIGURABLE, IS_AUFTRAGSART)
	VALUES (64, 'Wegfall Einwahlaccount', 1, 1, 2, 1, 0);




