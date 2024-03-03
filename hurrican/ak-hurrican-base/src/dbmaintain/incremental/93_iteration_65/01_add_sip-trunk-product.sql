-- SQL-Script, um das Produkt "SIP-Trunk" in Hurrican anzulegen

-- Produkt in T_PRODUKT einfügen

insert into T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, PROD_NAME_PATTERN,
LEITUNGSART, AKTIONS_ID, MAX_DN_COUNT, DN_BLOCK, DN_TYP,
GUELTIG_VON, GUELTIG_BIS, AUFTRAGSERSTELLUNG, LTGNR_ANLEGEN,
BRAUCHT_BUENDEL, ELVERLAUF, ENDSTELLEN_TYP, BESCHREIBUNG, ACCOUNT_VORS,
LI_NR, VPN_PHYSIK, PROJEKTIERUNG, IS_PARENT, CHECK_CHILD, IS_COMBI_PRODUKT, AUTO_PRODUCT_CHANGE,
EXPORT_KDP_M, CREATE_KDP_ACCOUNT_REPORT, EXPORT_AK_PRODUKTE, VERTEILUNG_DURCH, BA_RUECKLAEUFER,
PROJEKTIERUNG_CHAIN_ID, VERLAUF_CHAIN_ID, VERLAUF_CANCEL_CHAIN_ID, BA_CHANGE_REALDATE,
CREATE_AP_ADDRESS, ASSIGN_IAD, CPS_PROVISIONING, CPS_PROD_NAME, CPS_AUTO_CREATION,
CPS_ACCOUNT_TYPE, CPS_DSL_PRODUCT, TDN_KIND_OF_USE_PRODUCT, TDN_KIND_OF_USE_TYPE, TDN_USE_FROM_MASTER,
VIER_DRAHT,VERSION,MIN_DN_COUNT,TDN_KIND_OF_USE_TYPE_VPN,CPS_IP_DEFAULT,CPS_MULTI_DRAHT,IP_POOL,IP_PURPOSE_V4,
IP_PURPOSE_V4_EDITABLE,IP_NETMASK_SIZE_V4,IP_NETMASK_SIZE_V6,IP_NETMASK_SIZE_EDITABLE,ABRECHNUNG_IN_HURRICAN,
AUTOMATION_POSSIBLE )
values (
70,                                     -- PROD_ID
4,                                      -- PRODUKTGRUPPE_ID
null,                                   -- PRODUKT_NR
'SIP-Trunk',                            -- ANSCHLUSSART
null,                                   -- PROD_NAME_PATTERN
null,                                   -- LEITUNGSART
1,                                      -- AKTIONS_ID
0,                                   -- MAX_DN_COUNT
0,                                      -- DN_BLOCK
NULL,                                   -- DN_TYP
to_date('01.11.2012', 'dd.mm.yyyy'),    -- GUELTIG_VON
to_date('01.01.2200', 'dd.mm.yyyy'),    -- GUELTIG_BIS
0,                                      -- AUFTRAGSERSTELLUNG
1,                                      -- LTGNR_ANLEGEN
0,                                      -- BRAUCHT_BUENDEL
1,                                      -- ELVERLAUF
1,                                      -- ENDSTELLEN_TYP
null,                                   -- BESCHREIBUNG
'XS',                                   -- ACCOUNT_VORS
4,                                      -- LI_NR
0,                                      -- VPN_PHYSIK
0,                                      -- PROJEKTIERUNG
1,                                      -- IS_PARENT
0,                                      -- CHECK_CHILD
0,                                      -- IS_COMBI_PRODUKT
0,                                      -- AUTO_PRODUCT_CHANGE
1,                                      -- EXPORT_KDP_M
1,                                      -- CREATE_KDP_ACCOUNT_REPORT
null,                                   -- EXPORT_AK_PRODUKTE
4,                                      -- VERTEILUNG_DURCH
1,                                      -- BA_RUECKLAEUFER
null,                                   -- PROJEKTIERUNG_CHAIN_ID
17,                                     -- VERLAUF_CHAIN_ID
15,                                     -- VERLAUF_CANCEL_CHAIN_ID
1,                                      -- BA_CHANGE_REALDATE
0,                                      -- CREATE_AP_ADDRESS
0,                                      -- ASSIGN_IAD
1,                                      -- CPS_PROVISIONING
'SIP-Trunk',                            -- CPS_PROD_NAME
0,                                      -- CPS_AUTO_CREATION
'SDSL',                                 -- CPS_ACCOUNT_TYPE
1,                                      -- CPS_DSL_PRODUCT
'W',                                    -- TDN_KIND_OF_USE_PRODUCT
'S',                                    -- TDN_KIND_OF_USE_TYPE
null,                                   -- TDN_USE_FROM_MASTER  -- ab jetzt neu
0,                                      -- VIER_DRAHT
4,                                      -- VERSION
0,                                      -- MIN_DN_COUNT
null,                                   -- TDN_KIND_OF_USE_TYPE_VPN
1,                                      -- CPS_IP_DEFAULT
1,                                      -- CPS_MULTI_DRAHT
1,                                      -- IP_POOL
22371,                                  -- IP_PURPOSE_V4
1,                                      -- IP_PURPOSE_V4_EDITABLE
32,                                     -- IP_NETMASK_SIZE_V4
64,                                     -- IP_NETMASK_SIZE_V6
'1',                                    -- IP_NETMASK_SIZE_EDITABLE
null,                                   -- ABRECHNUNG_IN_HURRICAN
'0'                                     -- AUTOMATION_POSSIBLE
);

