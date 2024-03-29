alter table t_produkt modify anschlussart varchar2(40);

Insert into T_PRODUKT
   (PROD_ID, PRODUKTGRUPPE_ID, ANSCHLUSSART, PROD_NAME_PATTERN, AKTIONS_ID, MAX_DN_COUNT, DN_BLOCK, DN_TYP,
   BRAUCHT_DN4ACCOUNT, GUELTIG_VON, GUELTIG_BIS, AUFTRAGSERSTELLUNG, LTGNR_ANLEGEN,
   BRAUCHT_BUENDEL, ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP,
   ACCOUNT_VORS, LI_NR, VPN_PHYSIK, PROJEKTIERUNG, IS_PARENT,
   CHECK_CHILD, IS_COMBI_PRODUKT, AUTO_PRODUCT_CHANGE, EXPORT_KDP_M, CREATE_KDP_ACCOUNT_REPORT,
   VERTEILUNG_DURCH, BA_RUECKLAEUFER, VERLAUF_CHAIN_ID,
   VERLAUF_CANCEL_CHAIN_ID, BA_CHANGE_REALDATE, CREATE_AP_ADDRESS, ASSIGN_IAD, CPS_PROVISIONING,
   CPS_PROD_NAME, STATUSMELDUNGEN, CPS_AUTO_CREATION, CPS_ACCOUNT_TYPE, CPS_DSL_PRODUCT,
   TDN_KIND_OF_USE_PRODUCT, TDN_KIND_OF_USE_TYPE, VIER_DRAHT, VERSION, MIN_DN_COUNT, CPS_IP_DEFAULT, CPS_MULTI_DRAHT)
 Values
   (540, 21, 'Premium Glasfaser-DSL Doppel-Flat', 'Premium Glasfaser-DSL Doppel-Flat {DOWNSTREAM}',
    1, 10, '0', 60,
    '0', TO_DATE('02/01/2011 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), '0', '1',
    '0', '1', '0', 1,
    'X9', 1, '1', '0', '0',
    '0', '0', '0', '1', '1',
    4, '0', 45,
    15, '1', '0', '0', '1',
    'PremiumGlasfaserDSL', '1', '1', 'MAXI', '1',
    'W', 'V', '0', 0, 1, '1', '0');

-- DSLAM Profile 50/2,5 anlegen
Insert into T_DSLAM_PROFILE
   (ID, NAME, DOWNSTREAM, UPSTREAM, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS, GUELTIG, VERSION, ADSL1FORCE)
 Values
   (S_T_DSLAM_PROFILE_0.nextVal, 'MD_50000_2500_H', '50000', '2500',
    '0', 24, 114, '1', 0, '0');
Insert into T_DSLAM_PROFILE
   (ID, NAME, DOWNSTREAM, UPSTREAM, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS, GUELTIG, VERSION, ADSL1FORCE, BAUGRUPPEN_TYP)
 Values
   (S_T_DSLAM_PROFILE_0.nextVal, 'MD_50000_2500_H', '50000', '2500',
    '0', 24, 114, '1', 0, '0', 151);
Insert into T_DSLAM_PROFILE
   (ID, NAME, DOWNSTREAM, UPSTREAM, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS, GUELTIG, VERSION, ADSL1FORCE, BAUGRUPPEN_TYP)
 Values
   (S_T_DSLAM_PROFILE_0.nextVal, 'MD_50000_2500_H', '50000', '2500',
    '0', 24, 114, '1', 0, '0', 37);
Insert into T_DSLAM_PROFILE
   (ID, NAME, DOWNSTREAM, UPSTREAM, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS, GUELTIG, VERSION, ADSL1FORCE, BAUGRUPPEN_TYP)
 Values
   (S_T_DSLAM_PROFILE_0.nextVal, 'MD_50000_2500_H', '50000', '2500',
    '0', 24, 114, '1', 0, '0', 172);

-- DSLAM Profile zuordnen
Insert into T_PROD_2_DSLAMPROFILE
   (PROD_ID, DSLAM_PROFILE_ID)
 Values
   (540, 85);
Insert into T_PROD_2_DSLAMPROFILE
   (PROD_ID, DSLAM_PROFILE_ID)
 Values
   (540, 86);
Insert into T_PROD_2_DSLAMPROFILE
   (PROD_ID, DSLAM_PROFILE_ID)
 Values
   (540, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_50000_2500_H' and d.BAUGRUPPEN_TYP is null));

-- Produktaenderungen konfigurieren
Insert into T_PROD_2_PROD
   (ID, PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID, VERSION)
 Values
   (S_T_PROD_2_PROD_0.nextVal, 513, 540, 5000, 1, 0);
Insert into T_PROD_2_PROD
   (ID, PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID, VERSION)
 Values
   (S_T_PROD_2_PROD_0.nextVal, 512, 540, 5000, 1, 0);
Insert into T_PROD_2_PROD
   (ID, PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID, VERSION)
 Values
   (S_T_PROD_2_PROD_0.nextVal, 540, 512, 5000, 1, 0);
Insert into T_PROD_2_PROD
   (ID, PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID, VERSION)
 Values
   (S_T_PROD_2_PROD_0.nextVal, 540, 513, 5000, 1, 0);


-- techn. Leistungen dem Produkt zuordnen
Insert into T_PROD_2_TECH_LEISTUNG
   (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
 Values
   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 23, '0', 0);
Insert into T_PROD_2_TECH_LEISTUNG
   (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
 Values
   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 24, '0', 0);
Insert into T_PROD_2_TECH_LEISTUNG
   (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 Values
   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 114, 23, '1', 0);
Insert into T_PROD_2_TECH_LEISTUNG
   (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 Values
   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 114, 24, '1', 0);
Insert into T_PROD_2_TECH_LEISTUNG
   (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 Values
   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 27, 24, '0', 0);
Insert into T_PROD_2_TECH_LEISTUNG
   (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
 Values
   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 3, '0', 0);
Insert into T_PROD_2_TECH_LEISTUNG
   (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
 Values
   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 115, '0', 0);
Insert into T_PROD_2_TECH_LEISTUNG
   (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
 Values
   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 112, '0', 0);
Insert into T_PROD_2_TECH_LEISTUNG
   (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 Values
   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 113, 112, '1', 0);
Insert into T_PROD_2_TECH_LEISTUNG
   (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 Values
   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 4, null, '0', 0);

-- Equipment-Konfig definieren
Insert into T_PRODUKT_EQ_CONFIG
   (ID, PROD_ID, CONFIG_GROUP, EQ_TYP, EQ_PARAM, EQ_VALUE, VERSION)
 Values
   (S_T_PRODUKT_EQ_CONFIG_0.nextVal, 540, 1, 'EQ_OUT', 'carrier', 'MNET', 0);
Insert into T_PRODUKT_EQ_CONFIG
   (ID, PROD_ID, CONFIG_GROUP, EQ_TYP, EQ_PARAM, EQ_VALUE, VERSION)
 Values
   (S_T_PRODUKT_EQ_CONFIG_0.nextVal, 540, 1, 'EQ_OUT', 'rangSSType', 'FttB', 0);

-- Physikzuordnung
Insert into T_PRODUKT_2_PHYSIKTYP
   (ID, PROD_ID, PHYSIKTYP, VIRTUELL, VERSION)
 Values
   (S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, 540, 800, '0', 0);
Insert into T_PRODUKT_2_PHYSIKTYP
   (ID, PROD_ID, PHYSIKTYP, VIRTUELL, VERSION)
 Values
   (S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, 540, 803, '0', 0);
Insert into T_PRODUKT_2_PHYSIKTYP
   (ID, PROD_ID, PHYSIKTYP, VIRTUELL, VERSION)
 Values
   (S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, 540, 804, '0', 0);

-- Standorttypen definieren
Insert into T_PRODUKT_2_TECH_LOCATION_TYPE
   (ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW, DATEW, VERSION)
 Values
   (S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal, 540, 11011, 1, 'IMPORT', SYSDATE, 0);
Insert into T_PRODUKT_2_TECH_LOCATION_TYPE
   (ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW, DATEW, VERSION)
 Values
   (S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal, 540, 11002, 2, 'IMPORT', SYSDATE, 0);
Insert into T_PRODUKT_2_TECH_LOCATION_TYPE
   (ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW, DATEW, VERSION)
 Values
   (S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal, 540, 11013, 3, 'IMPORT', SYSDATE, 0);

-- Produktmapping
Insert into T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE, VERSION)
 Values (1044, 540, 540, 'phone', 0);

-- BA Anlaesse konfigurieren
Insert into T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) Values (540, 1);
Insert into T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) Values (540, 2);
Insert into T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) Values (540, 4);


-- BA Konfiguration
Insert into T_BA_VERL_CONFIG
   (ID, PROD_ID, ANLASS, ABT_CONFIG_ID, GUELTIG_VON, GUELTIG_BIS, DATEW, USERW, VERSION, AUTO_VERTEILEN, CPS_NECESSARY)
 Values
   (S_T_BA_VERL_CONFIG_0.nextVal, 540, 27, 18, SYSDATE,
    TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), SYSDATE, 'Import', 0, '1', '1');
Insert into T_BA_VERL_CONFIG
   (ID, PROD_ID, ANLASS, ABT_CONFIG_ID, GUELTIG_VON, GUELTIG_BIS, DATEW, USERW, VERSION, AUTO_VERTEILEN, CPS_NECESSARY)
 Values
   (S_T_BA_VERL_CONFIG_0.nextVal, 540, 13, 18, SYSDATE,
    TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), SYSDATE, 'Import', 0, '1', '1');

-- Schnittstelle definieren
Insert into T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) Values (540, 38);

-- Sperren
Insert into T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) Values (540, 2);
Insert into T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) Values (540, 3);

-- CPS
Insert into T_CPS_DATA_CHAIN_CONFIG
   (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
 Values
   (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 540, 14000, 53, 0);
Insert into T_CPS_DATA_CHAIN_CONFIG
   (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
 Values
   (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 540, 14001, 53, 0);
Insert into T_CPS_DATA_CHAIN_CONFIG
   (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
 Values
   (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 540, 14002, 54, 0);
Insert into T_CPS_DATA_CHAIN_CONFIG
   (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
 Values
   (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 540, 14006, 53, 0);

