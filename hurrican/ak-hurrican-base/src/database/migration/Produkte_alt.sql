--
-- SQL-Script zur Neuanlage der 'alten' DSL-Produkte als Kombi-Produkt
--

-- AK-ADSL
INSERT INTO T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, PROD_NAME_PATTERN, LEITUNGSART, AKTIONS_ID,
	BRAUCHT_DN, AUFTRAGSERSTELLUNG, LTGNR_VORS, LTGNR_ANLEGEN, BRAUCHT_BUENDEL, 
 	ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, ACCOUNT_VORS, LI_NR, VPN_PHYSIK, IS_PARENT,
 	CHECK_CHILD, IS_COMBI_PRODUKT, VERTEILUNG_DURCH, MAX_DN_COUNT, DN_BLOCK, DN_TYP, GUELTIG_VON, GUELTIG_BIS) 
 	VALUES (400, 3, '', 'AK-ADSL + ISDN', 'AK-ADSL {DOWNSTREAM} ISDN', null, 1, 1, 0, 'DV', 1, 0, 
 	1, 0, 1, 'as', 1, 1, 0, 0, 1, 4, 10, 0, 60, '2006-10-01', '2200-01-01');
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (400, 8);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (400,2);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (400,3);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP, PHYSIKTYP_ADDITIONAL) 
 	VALUES (400,7,8),(400,507,508),(400,513,508);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (400,13,8,1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (400,13,9,1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (400,13,10,1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (400,14,11,1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (400,1,null,1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (400,6,null,0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (400,8,null,0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (400,9,null,0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (400,10,null,0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (400,11,null,0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (400,100,null,1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (400,101,null,0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (400,103,null,0);
INSERT INTO T_BA_VERL_NEU (PROD_ID, EWSD, SDH, SCT, IPS) VALUES (400,1,1,1,1);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (400,1),(400,2),(400,4);
INSERT INTO T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) VALUES (400,10,1);
INSERT INTO T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) VALUES (400,11,1);
INSERT INTO T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) VALUES (400,13,0);

-- AK-DSLplus ISDN
INSERT INTO T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, PROD_NAME_PATTERN, LEITUNGSART, AKTIONS_ID,
 	BRAUCHT_DN, AUFTRAGSERSTELLUNG, LTGNR_VORS, LTGNR_ANLEGEN, BRAUCHT_BUENDEL, 
 	ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, ACCOUNT_VORS, LI_NR, VPN_PHYSIK, IS_PARENT,
 	CHECK_CHILD, IS_COMBI_PRODUKT, VERTEILUNG_DURCH, MAX_DN_COUNT, DN_BLOCK, DN_TYP, GUELTIG_VON, GUELTIG_BIS) 
 	VALUES (410, 16, '', 'AK-DSLplus + ISDN', 'AK-DSLplus {DOWNSTREAM} ISDN', null, 1, 1, 0, 'DV', 1, 0, 
 	1, 0, 1, 'as', 1, 1, 0, 0, 1, 4, 10, 0, 60, '2006-10-01', '2200-01-01');
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (410, 8);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (410,2);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (410,3);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP, PHYSIKTYP_ADDITIONAL) 
 	VALUES (410,7,8),(410,507,508),(410,513,508);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (410,13,8,1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (410,13,9,1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (410,13,10,1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (410,14,11,1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (410,1,null,1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (410,6,null,0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (410,8,null,0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (410,9,null,0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (410,10,null,0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (410,11,null,0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (410,100,null,1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (410,101,null,0);
INSERT INTO T_BA_VERL_NEU (PROD_ID, EWSD, SDH, SCT, IPS) VALUES (410,1,1,1,1);	
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (410,1),(410,2),(410,4);
INSERT INTO T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) VALUES (410,10,1);
INSERT INTO T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) VALUES (410,11,1);
INSERT INTO T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) VALUES (410,13,0);

-- AK-DSLplus analog
INSERT INTO T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, PROD_NAME_PATTERN, LEITUNGSART, AKTIONS_ID,
 	BRAUCHT_DN, AUFTRAGSERSTELLUNG, LTGNR_VORS, LTGNR_ANLEGEN, BRAUCHT_BUENDEL, 
 	ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, ACCOUNT_VORS, LI_NR, VPN_PHYSIK, IS_PARENT,
 	CHECK_CHILD, IS_COMBI_PRODUKT, VERTEILUNG_DURCH, MAX_DN_COUNT, DN_BLOCK, DN_TYP, GUELTIG_VON, GUELTIG_BIS) 
 	VALUES (411, 16, '', 'AK-DSLplus + analog', 'AK-DSLplus {DOWNSTREAM} analog', null, 1, 1, 0, 'DV', 1, 0, 
 	1, 0, 1, 'as', 1, 1, 0, 0, 1, 4, 1, 0, 60, '2006-10-01', '2200-01-01');
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (411, 8);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (411,2);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (411,3);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP, PHYSIKTYP_ADDITIONAL) 
 	VALUES (411,7,12),(411,507,512),(411,513,512);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (411,13,8,1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (411,13,9,1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (411,13,10,1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (411,14,11,1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (411,1,null,1);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (411,6,null,0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (411,8,null,0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (411,9,null,0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (411,10,null,0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (411,11,null,0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (411,102,null,1);
INSERT INTO T_BA_VERL_NEU (PROD_ID, EWSD, SDH, SCT, IPS) VALUES (411,1,1,1,1);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (411,1),(411,2),(411,4);
INSERT INTO T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) VALUES (411,11,1);
INSERT INTO T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) VALUES (411,13,0);


-- Produktwechsel (AK-ADSL auf Maxi/Premium ISDN)
-- Anschlussuebernahme
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (400,420,5000,1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (400,430,5000,1);
-- DSL-Kreuzung (AK-ADSL auf Maxi/Premium analog)
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (400,421,5005,5);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (400,431,5005,5);

-- Produktwechsel 
-- Anschlussuebernahme (AK-DSLplus ISDN auf Maxi/Premium ISDN)
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (410,420,5000,1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (410,430,5000,1);
-- Anschlussuebernahme (AK-DSLplus analog auf Maxi/Premium analog)
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (411,421,5000,1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (411,431,5000,1);
-- DSL-Kreuzung (AK-DSLplus ISDN auf Maxi/Premium analog)
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (410,421,5005,5);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (410,431,5005,5);
-- DSL-Kreuzung (AK-DSLplus analog auf Maxi/Premium ISDN)
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (411,420,5005,5);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (411,430,5005,5);


-- ProduktMappings konfigurieren
-- AK-ADSL
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE) values (1005, 1, 400, 'phone');
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE) values (1005, 400, 400, 'dsl');
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE) values (1006, 2, 400, 'phone');
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE) values (1006, 400, 400, 'dsl');
-- AK-DSLplus ISDN
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE) values (1007, 319, 410, 'phone');
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE) values (1007, 410, 410, 'dsl');
-- AK-DSLplus analog
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE) values (1008, 318, 411, 'phone');
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE) values (1008, 410, 411, 'dsl');


-- TODO Leistungsbuendel und Leistung__NO eintragen
-- DN-Leistungskonfig fuer AK-ADSL
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (2, 20602, 2003, 'AK-ISDN EXTRA MSN');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (2, 20617, 2003, 'AK-ISDN KOMFORT MSN');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (2, 20634, 2003, 'AK-ISDN PREMIUM MSN');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (2, 20636, 2003, 'AK-ISDN PREMIUM SDSL');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (2, 20640, 2003, 'AK-ISDN PROFI MSN');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (2, 20660, 2003, 'AK-ISDN PROFI SDSL');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (2, 20668, 2003, 'AK-ISDN TOP MSN');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (2, 20453, 2003, 'AK-ISDN TOP-RV1 MSN');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (2, 20456, 2003, 'AK-ISDN TOP-RV1 SDSL');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (2, 20676, 2003, 'AK-ISDN TOP SDSL');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (2, 20651, 2003, 'AK-ISDN TOP DSLconnect');

-- TK DSS1
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (3, 20601, 2003, 'AK-ISDN EXTRA TK');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (3, 20616, 2003, 'AK-ISDN KOMFORT TK');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (3, 20635, 2003, 'AK-ISDN PREMIUM TK');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (3, 20639, 2003, 'AK-ISDN PROFI TK');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (3, 20665, 2003, 'AK-ISDN TOP TK');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (3, 20455, 2003, 'AK-ISDN TOP-RV1 TK');

-- TK 1TR6
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (7, 20601, 20687, 2003, 'AK-ISDN EXTRA TK 1TR6');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (7, 20616, 20687, 2003, 'AK-ISDN KOMFORT TK 1TR6');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (7, 20635, 20687, 2003, 'AK-ISDN PREMIUM TK 1TR6');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (7, 20639, 20687, 2003, 'AK-ISDN PROFI TK 1TR6');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (7, 20665, 20687, 2003, 'AK-ISDN TOP TK 1TR6');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (7, 20455, 20687, 2003, 'AK-ISDN TOP-RV1 TK 1TR6');

-- S2M DSS1
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (8, 20598, 2003, 'AK-ISDN EXTRA S2M');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (8, 20628, 2003, 'AK-ISDN KOMFORT S2M');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (8, 20633, 2003, 'AK-ISDN PREMIUM S2M');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (8, 20637, 2003, 'AK-ISDN PROFI S2M');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (8, 20666, 2003, 'AK-ISDN TOP S2M');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (8, 20454, 2003, 'AK-ISDN TOP-RV1 S2M');

-- S2M 1TR6
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (7, 20598, 20687, 2003, 'AK-ISDN EXTRA S2M 1TR6');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (7, 20628, 20687, 2003, 'AK-ISDN KOMFORT S2M 1TR6');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (7, 20633, 20687, 2003, 'AK-ISDN PREMIUM S2M 1TR6');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (7, 20637, 20687, 2003, 'AK-ISDN PROFI S2M 1TR6');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (7, 20666, 20687, 2003, 'AK-ISDN TOP S2M 1TR6');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (7, 20454, 20687, 2003, 'AK-ISDN TOP-RV1 S2M 1TR6');



-- DN-Leistungskonfig fuer AK-DSLplus ISDN
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (2, 20854, 2004, 'AK-DSLplus ISDN MSN EXTRA');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (2, 20856, 2004, 'AK-DSLplus ISDN MSN KOMFORT');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (2, 20858, 2004, 'AK-DSLplus ISDN MSN PREMIUM');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (2, 20861, 2004, 'AK-DSLplus ISDN MSN PROFI');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (2, 20864, 2004, 'AK-DSLplus ISDN MSN TOP');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (2, 20862, 2004, 'AK-DSLplus ISDN MSN talkflat');

insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (1, 20855, 2004, 'AK-DSLplus analog EXTRA');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (1, 20857, 2004, 'AK-DSLplus analog KOMFORT');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (1, 20859, 2004, 'AK-DSLplus analog PREMIUM');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (1, 20860, 2004, 'AK-DSLplus analog PROFI');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (1, 20865, 2004, 'AK-DSLplus analog TOP');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) VALUES (1, 20863, 2004, 'AK-DSLplus analog talflat');


-- TODO: Rangierungsmatrix ueber GUI erstellen


-- Strassenkonfiguration
create table p2sl_tmp (
	prod_id integer(9) not null,
	sl_id integer(9) not null,
	freigabe_id integer(9) not null);
-- Konfiguration fuer AK-ADSL
insert into p2sl_tmp (prod_id, sl_id, freigabe_id)
	select 400, src.sl_id, src.freigabe_id from t_prod_2_sl src where src.prod_id=57;
insert into t_prod_2_sl (prod_id, sl_id, freigabe_id) 
	select src.prod_id, src.sl_id, src.freigabe_id from p2sl_tmp src;
delete from p2sl_tmp;
-- Konfiguration fuer AK-DSLplus ISDN
insert into p2sl_tmp (prod_id, sl_id, freigabe_id)
	select 410, src.sl_id, src.freigabe_id from t_prod_2_sl src where src.prod_id=317;
insert into t_prod_2_sl (prod_id, sl_id, freigabe_id) 
	select src.prod_id, src.sl_id, src.freigabe_id from p2sl_tmp src;
delete from p2sl_tmp;
-- Konfiguration fuer AK-DSLplus analog
insert into p2sl_tmp (prod_id, sl_id, freigabe_id)
	select 411, src.sl_id, src.freigabe_id from t_prod_2_sl src where src.prod_id=317;
insert into t_prod_2_sl (prod_id, sl_id, freigabe_id) 
	select src.prod_id, src.sl_id, src.freigabe_id from p2sl_tmp src;
delete from p2sl_tmp;
-- temp. Tabelle wieder entfernen
drop table p2sl_tmp;

