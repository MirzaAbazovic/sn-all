--
-- Script erstellt neue ConnectDSL Produkte
--

alter session set nls_date_format='yyyy-mm-dd';

INSERT INTO T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, LEITUNGSART, PROD_NAME_PATTERN, AKTIONS_ID,
    BRAUCHT_DN, AUFTRAGSERSTELLUNG, LTGNR_VORS, LTGNR_ANLEGEN, BRAUCHT_BUENDEL,
    ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, ACCOUNT_VORS, LI_NR, VPN_PHYSIK, IS_PARENT, PROJEKTIERUNG,
    CHECK_CHILD, VERTEILUNG_DURCH, EXPORT_KDP_M, AUTO_PRODUCT_CHANGE, IS_COMBI_PRODUKT, GUELTIG_VON, GUELTIG_BIS)
    VALUES (460, 4, '', 'ConnectDSL SDSL', null, 'ConnectDSL SDSL {DOWNSTREAM}', 1,
    0, 0, 'DV', 1, 1,
    1, 0, 1, 'X', 4, 1, 1, 1,
    0, 4, 0, 0, 0, '2009-01-01', '2200-01-01');
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (460, 1);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (460,2);
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (460, 10);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (460,2);

-- Endgeraete-Konfig
INSERT INTO T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT) VALUES (S_T_PROD_2_EG_0.nextVal, 460, 28, 0);
INSERT INTO T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT) VALUES (S_T_PROD_2_EG_0.nextVal, 460, 12, 0);

insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE, STR_VALUE,
    DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, PROD_NAME_STR)
    values (30, '256 kbit/s', 10031, 'DOWNSTREAM', 256, '256',
    '0', '0', '1', '0', '0', '1', '2009-01-01', '2200-01-01', '256');
insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE, STR_VALUE,
    DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, PROD_NAME_STR)
    values (31, '512 kbit/s', 10032, 'DOWNSTREAM', 512, '512',
    '0', '0', '1', '0', '0', '1', '2009-01-01', '2200-01-01', '512');

-- Zuordnung techn. Leistungen zu Produkt
-- Downstreams
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 460, 8, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 460, 9, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 460, 10, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 460, 11, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 460, 30, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 460, 31, null, 0);
-- ISDN-Backup
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 460, 17, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 460, 19, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 460, 20, null, 0);
-- QoS
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 460, 50, null, 0);

-- Physikzuordnung
INSERT INTO T_PRODUKT_2_PHYSIKTYP (ID, PROD_ID, PHYSIKTYP) VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, 460,9);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (ID, PROD_ID, PHYSIKTYP) VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, 460,509);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (ID, PROD_ID, PHYSIKTYP) VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, 460,515);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (ID, PROD_ID, PHYSIKTYP) VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, 460,106);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (ID, PROD_ID, PHYSIKTYP) VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, 460,107);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (ID, PROD_ID, PHYSIKTYP) VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, 460,108);

-- Mapping fuer Produkt aufnehmen, wenn in Taifun eingerichtet
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE) VALUES (500, 460, 460, 'sdsl');

--
-- ##################################
--

INSERT INTO T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, LEITUNGSART, PROD_NAME_PATTERN, AKTIONS_ID,
    BRAUCHT_DN, AUFTRAGSERSTELLUNG, LTGNR_VORS, LTGNR_ANLEGEN, BRAUCHT_BUENDEL,
    ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, ACCOUNT_VORS, LI_NR, VPN_PHYSIK, IS_PARENT, PROJEKTIERUNG,
    CHECK_CHILD, VERTEILUNG_DURCH, EXPORT_KDP_M, AUTO_PRODUCT_CHANGE, IS_COMBI_PRODUKT, GUELTIG_VON, GUELTIG_BIS)
    VALUES (461, 3, '', 'ConnectDSL ADSL', null, 'ConnectDSL ADSL {DOWNSTREAM}', 1,
    0, 0, 'DV', 1, 1,
    1, 0, 1, 'X', 1, 1, 1, 1,
    0, 4, 0, 0, 0, '2009-01-01', '2200-01-01');
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (461, 1);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (461,2);
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (461, 10);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (461,2);

-- Zuordnung techn. Leistungen zu Produkt
-- Downstreams
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 461, 8, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 461, 9, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 461, 10, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 461, 11, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 461, 12, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 461, 21, null, 0);
-- Upstreams
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 461, 13, 8, 1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 461, 13, 9, 1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 461, 13, 10, 1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 461, 14, 11, 1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 461, 15, 12, 1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 461, 22, 21, 1);
-- ISDN-Backup
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 461, 17, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 461, 19, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 461, 20, null, 0);
-- QoS
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 461, 50, null, 0);

-- Physikzuordnung
INSERT INTO T_PRODUKT_2_PHYSIKTYP (ID, PROD_ID, PHYSIKTYP) VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, 461,514);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (ID, PROD_ID, PHYSIKTYP) VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, 461,105);

-- Mapping fuer Produkt aufnehmen, wenn in Taifun eingerichtet
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE) VALUES (500, 461, 461, 'adsl');

--
-- ######################
--

insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE, STR_VALUE,
    DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, PROD_NAME_STR)
    values (32, '6800 kbit/s', 10033, 'DOWNSTREAM', 6800, '6800',
    '0', '0', '1', '0', '0', '1', '2009-01-01', '2200-01-01', '6800');
insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE, STR_VALUE,
    DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, PROD_NAME_STR)
    values (33, '10000 kbit/s', 10034, 'DOWNSTREAM', 10000, '10000',
    '0', '0', '1', '0', '0', '1', '2009-01-01', '2200-01-01', '10000');

INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 441, 32, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 441, 33, null, 0);


