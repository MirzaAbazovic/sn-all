-- Produktdefinition
INSERT INTO T_PRODUKT
   (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, PROD_NAME_PATTERN, 
    LEITUNGSART, AKTIONS_ID, MAX_DN_COUNT, DN_BLOCK, DN_TYP, 
    BRAUCHT_DN4ACCOUNT, GUELTIG_VON, GUELTIG_BIS, AUFTRAGSERSTELLUNG, LTGNR_ANLEGEN, 
    BRAUCHT_BUENDEL, ELVERLAUF, ENDSTELLEN_TYP, BESCHREIBUNG, ACCOUNT_VORS, 
    LI_NR, VPN_PHYSIK, PROJEKTIERUNG, IS_PARENT, CHECK_CHILD, 
    IS_COMBI_PRODUKT, AUTO_PRODUCT_CHANGE, EXPORT_KDP_M, CREATE_KDP_ACCOUNT_REPORT, EXPORT_AK_PRODUKTE, 
    VERTEILUNG_DURCH, BA_RUECKLAEUFER, PROJEKTIERUNG_CHAIN_ID, VERLAUF_CHAIN_ID, VERLAUF_CANCEL_CHAIN_ID, 
    BA_CHANGE_REALDATE, CREATE_AP_ADDRESS, ASSIGN_IAD, CPS_PROVISIONING, CPS_PROD_NAME, 
    CPS_AUTO_CREATION, CPS_ACCOUNT_TYPE, CPS_DSL_PRODUCT, TDN_KIND_OF_USE_PRODUCT, TDN_KIND_OF_USE_TYPE, 
    TDN_USE_FROM_MASTER, VIER_DRAHT, VERSION, MIN_DN_COUNT, TDN_KIND_OF_USE_TYPE_VPN, 
    CPS_IP_DEFAULT, CPS_MULTI_DRAHT, IP_POOL, IP_PURPOSE_V4, IP_PURPOSE_V4_EDITABLE, 
    IP_NETMASK_SIZE_V4, IP_NETMASK_SIZE_V6, IP_NETMASK_SIZE_EDITABLE, ABRECHNUNG_IN_HURRICAN, AUTOMATION_POSSIBLE)
 VALUES
   (480, 17, NULL, 'DSL + VoIP', 'DSL {DOWNSTREAM} VoIP', 
    NULL, 1, 10, '0', 60, 
    0, TO_DATE('01/24/2013 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), '0', '1', 
    '0', '1', 1, NULL, 'XA', 
    1, '1', '0', '0', '0', 
    '1', '1', '1', '1', NULL, 
    4, '0', NULL, NULL, NULL, 
    '1', '0', '0', '1', 'dsl+voip', 
    '1', 'MAXI', '1', 'W', 'A', 
    NULL, '0', 0, 1, NULL, 
    '0', '0', 1, 22371, '0', 
    32, 48, '0', NULL, '1')
;

-- Schnittstelle
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE
    (PROD_ID, SCHNITTSTELLE_ID)
  VALUES
    (480, 8)
;	

-- EQ Config
INSERT INTO T_PRODUKT_EQ_CONFIG
   (ID, PROD_ID, CONFIG_GROUP, EQ_TYP, EQ_PARAM, 
    EQ_VALUE, VERSION, IS_RANGIERUNGSPART_DEFAULT, IS_RANGIERUNGSPART_ADDITIONAL)
 VALUES
   (S_T_PRODUKT_EQ_CONFIG_0.nextVal, 480, 1, 'EQ_OUT', 'rangSchnittstelle', 
    'H', 0, '1', '0')
;
INSERT INTO T_PRODUKT_EQ_CONFIG
   (ID, PROD_ID, CONFIG_GROUP, EQ_TYP, EQ_PARAM, 
    EQ_VALUE, VERSION, IS_RANGIERUNGSPART_DEFAULT, IS_RANGIERUNGSPART_ADDITIONAL)
 VALUES
   (S_T_PRODUKT_EQ_CONFIG_0.nextVal, 480, 1, 'EQ_OUT', 'status', 
    'frei', 0, '1', '0')
;

-- SIP-DOMAIN
INSERT INTO T_PROD_2_SIP_DOMAIN
   (ID, PROD_ID, SWITCH_REF_ID, SIP_DOMAIN_REF_ID, USERW, 
    DATEW, VERSION, DEFAULT_DOMAIN)
 VALUES
   (S_T_PROD_2_SIP_DOMAIN_0.nextVal, 480, 22208, 22344, 'IMPORT', 
    TO_DATE('01/31/2013 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, '1')
;

-- Sperren
INSERT INTO T_SPERRE_VERTEILUNG
   (PROD_ID, ABTEILUNG_ID)
 VALUES
   (480, 2)
;

