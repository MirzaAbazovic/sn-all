-- SQL-Script, um ein Housing Produkt in Hurrican anzulegen

insert into T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, PROD_NAME_PATTERN,
LEITUNGSART, AKTIONS_ID, BRAUCHT_DN, DN_MOEGLICH, MAX_DN_COUNT, DN_BLOCK, DN_TYP,
BRAUCHT_DN4ACCOUNT, GUELTIG_VON, GUELTIG_BIS, AUFTRAGSERSTELLUNG, LTGNR_VORS, LTGNR_ANLEGEN,
BRAUCHT_BUENDEL, ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, BESCHREIBUNG, ACCOUNT_VORS,
LI_NR, VPN_PHYSIK, PROJEKTIERUNG, IS_PARENT, CHECK_CHILD, IS_COMBI_PRODUKT, AUTO_PRODUCT_CHANGE,
EXPORT_KDP_M, CREATE_KDP_ACCOUNT_REPORT, EXPORT_AK_PRODUKTE, VERTEILUNG_DURCH, BA_RUECKLAEUFER,
BRAUCHT_VPI_VCI, PROJEKTIERUNG_CHAIN_ID, VERLAUF_CHAIN_ID, VERLAUF_CANCEL_CHAIN_ID, BA_CHANGE_REALDATE,
CREATE_AP_ADDRESS, ASSIGN_IAD, CPS_PROVISIONING, CPS_PROD_NAME, STATUSMELDUNGEN, CPS_AUTO_CREATION,
CPS_ACCOUNT_TYPE, CPS_DSL_PRODUCT, TDN_KIND_OF_USE_PRODUCT, TDN_KIND_OF_USE_TYPE, TDN_USE_FROM_MASTER)
values (
360,                                    -- PROD_ID
2,                                      -- PRODUKTGRUPPE_ID
null,                                   -- PRODUKT_NR
'Housing',                              -- ANSCHLUSSART
'Housing',                              -- PROD_NAME_PATTERN
null,                                   -- LEITUNGSART
1,                                      -- AKTIONS_ID
0,                                      -- BRAUCHT_DN
0,                                      -- DN_MOEGLICH
NULL,                                   -- MAX_DN_COUNT
0,                                      -- DN_BLOCK
NULL,                                   -- DN_TYP
0,                                      -- BRAUCHT_DN4ACCOUNT
to_date('01.02.2010', 'dd.mm.yyyy'),    -- GUELTIG_VON
to_date('01.01.2200', 'dd.mm.yyyy'),    -- GUELTIG_BIS
0,                                      -- AUFTRAGSERSTELLUNG
NULL,                                   -- LTGNR_VORS
0,                                      -- LTGNR_ANLEGEN
0,                                      -- BRAUCHT_BUENDEL
1,                                      -- ELVERLAUF
0,                                      -- ABRECHNUNG_IN_HURRICAN
0,                                      -- ENDSTELLEN_TYP
null,                                   -- BESCHREIBUNG
null,                                   -- ACCOUNT_VORS
null,                                   -- LI_NR
0,                                      -- VPN_PHYSIK
1,                                      -- PROJEKTIERUNG
0,                                      -- IS_PARENT
0,                                      -- CHECK_CHILD
0,                                      -- IS_COMBI_PRODUKT
0,                                      -- AUTO_PRODUCT_CHANGE
0,                                      -- EXPORT_KDP_M
0,                                      -- CREATE_KDP_ACCOUNT_REPORT
null,                                   -- EXPORT_AK_PRODUKTE
11,                                     -- VERTEILUNG_DURCH
1,                                      -- BA_RUECKLAEUFER
0,                                      -- BRAUCHT_VPI_VCI
null,                                   -- PROJEKTIERUNG_CHAIN_ID
15,                                     -- VERLAUF_CHAIN_ID
15,                                     -- VERLAUF_CANCEL_CHAIN_ID
1,                                      -- BA_CHANGE_REALDATE
0,                                      -- CREATE_AP_ADDRESS
0,                                      -- ASSIGN_IAD
0,                                      -- CPS_PROVISIONING
null,                                   -- CPS_PROD_NAME
null,                                   -- STATUSMELDUNGEN
0,                                      -- CPS_AUTO_CREATION
null,                                   -- CPS_ACCOUNT_TYPE
0,                                      -- CPS_DSL_PRODUCT
NULL,                                   -- TDN_KIND_OF_USE_PRODUCT
NULL,                                   -- TDN_KIND_OF_USE_TYPE
null                                    -- TDN_USE_FROM_MASTER
);


