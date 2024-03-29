--
-- DB-Aenderungen fuer das Produkt Maxi Deluxe
--


-- Produktanlage
alter session set nls_date_format = 'yyyy-mm-dd';
insert into T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, PROD_NAME_PATTERN, LEITUNGSART, AKTIONS_ID, BRAUCHT_DN, DN_MOEGLICH, MAX_DN_COUNT, DN_BLOCK, DN_TYP, BRAUCHT_DN4ACCOUNT, GUELTIG_VON, GUELTIG_BIS, AUFTRAGSERSTELLUNG, LTGNR_VORS, LTGNR_ANLEGEN, BRAUCHT_BUENDEL, ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, BESCHREIBUNG, ACCOUNT_VORS, LI_NR, VPN_PHYSIK, PROJEKTIERUNG, IS_PARENT, CHECK_CHILD, IS_COMBI_PRODUKT, AUTO_PRODUCT_CHANGE, EXPORT_KDP_M, CREATE_KDP_ACCOUNT_REPORT, EXPORT_AK_PRODUKTE, VERTEILUNG_DURCH, BA_RUECKLAEUFER, BRAUCHT_VPI_VCI, PROJEKTIERUNG_CHAIN_ID, VERLAUF_CHAIN_ID, VERLAUF_CANCEL_CHAIN_ID, BA_CHANGE_REALDATE, CREATE_AP_ADDRESS, ASSIGN_IAD)
values (511, 21, null, 'Maxi Telefon', 'Maxi Telefon', null, 1, 1, 0, 1, 0, 60, 0, '2009-06-01', '2200-01-01', 0, 'FD', 1, 0, 1, 0, 1, null, null, null, 1, 0, 0, 0, 0, 0, 1, 1, null, 4, 0, 0, null, 32, 15, 1, 0, 0);
insert into T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, PROD_NAME_PATTERN, LEITUNGSART, AKTIONS_ID, BRAUCHT_DN, DN_MOEGLICH, MAX_DN_COUNT, DN_BLOCK, DN_TYP, BRAUCHT_DN4ACCOUNT, GUELTIG_VON, GUELTIG_BIS, AUFTRAGSERSTELLUNG, LTGNR_VORS, LTGNR_ANLEGEN, BRAUCHT_BUENDEL, ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, BESCHREIBUNG, ACCOUNT_VORS, LI_NR, VPN_PHYSIK, PROJEKTIERUNG, IS_PARENT, CHECK_CHILD, IS_COMBI_PRODUKT, AUTO_PRODUCT_CHANGE, EXPORT_KDP_M, CREATE_KDP_ACCOUNT_REPORT, EXPORT_AK_PRODUKTE, VERTEILUNG_DURCH, BA_RUECKLAEUFER, BRAUCHT_VPI_VCI, PROJEKTIERUNG_CHAIN_ID, VERLAUF_CHAIN_ID, VERLAUF_CANCEL_CHAIN_ID, BA_CHANGE_REALDATE, CREATE_AP_ADDRESS, ASSIGN_IAD)
values (512, 21, null, 'Maxi Pur deluxe', 'Maxi Pur deluxe {DOWNSTREAM}', null, 1, 1, 0, 1, 0, 60, 0, '2009-06-01', '2200-01-01', 0, 'FD', 1, 0, 1, 0, 1, null, 'X9', 1, 1, 0, 0, 0, 0, 0, 1, 1, null, 4, 0, 0, null, 32, 15, 1, 0, 0);
insert into T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, PROD_NAME_PATTERN, LEITUNGSART, AKTIONS_ID, BRAUCHT_DN, DN_MOEGLICH, MAX_DN_COUNT, DN_BLOCK, DN_TYP, BRAUCHT_DN4ACCOUNT, GUELTIG_VON, GUELTIG_BIS, AUFTRAGSERSTELLUNG, LTGNR_VORS, LTGNR_ANLEGEN, BRAUCHT_BUENDEL, ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, BESCHREIBUNG, ACCOUNT_VORS, LI_NR, VPN_PHYSIK, PROJEKTIERUNG, IS_PARENT, CHECK_CHILD, IS_COMBI_PRODUKT, AUTO_PRODUCT_CHANGE, EXPORT_KDP_M, CREATE_KDP_ACCOUNT_REPORT, EXPORT_AK_PRODUKTE, VERTEILUNG_DURCH, BA_RUECKLAEUFER, BRAUCHT_VPI_VCI, PROJEKTIERUNG_CHAIN_ID, VERLAUF_CHAIN_ID, VERLAUF_CANCEL_CHAIN_ID, BA_CHANGE_REALDATE, CREATE_AP_ADDRESS, ASSIGN_IAD)
values (513, 21, null, 'Maxi Komplett deluxe', 'Maxi Komplett deluxe {DOWNSTREAM}', null, 1, 1, 0, 10, 0, 60, 0, '2009-06-01', '2200-01-01', 0, 'FD', 1, 0, 1, 0, 1, null, 'X9', 1, 1, 0, 0, 0, 0, 0, 1, 1, null, 4, 0, 0, null, 32, 15, 1, 0, 0);

-- Produkt-Schnittstelle
insert into T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) values (511, 7);
insert into T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) values (512, 38);
insert into T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) values (513, 38);

-- Sperre
insert into T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) values (511, 3);
insert into T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) values (512, 1);
insert into T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) values (512, 2);
insert into T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) values (512, 3);
insert into T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) values (513, 1);
insert into T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) values (513, 2);
insert into T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) values (513, 3);

-- Tech. Leistung
insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_MISC__NO, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE, STR_VALUE, PARAMETER, PROD_NAME_STR, DESCRIPTION, DISPO, EWSD, SDH, IPS, SCT, CHECK_QUANTITY, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS)
values (113, 'VoIP Maxi Deluxe 2. ab-Port', null, 20013, 'VOIP', 1, 'Maxi_Deluxe', 'Wirknetz/MaxiDeluxe/{0}/{1}', '2. ab-Port', 'VoIP Leistung fuer Deluxe Upgrade 2. Telefonanschluss', 0, 1, 0, 0, 0, null, 1, '2009-06-01', '2200-01-01');
insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_MISC__NO, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE, STR_VALUE, PARAMETER, PROD_NAME_STR, DESCRIPTION, DISPO, EWSD, SDH, IPS, SCT, CHECK_QUANTITY, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS)
values (114, '2500 kbit/s', null, 10028, 'UPSTREAM', 2500, '2500', null, '2500', 'Upstream-Bandbreite fuer Maxi Deluxe Produkte', '0', '0', '0', '1', '0', '0', '1', '2009-06-01', '2200-01-01');
insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_MISC__NO, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE, STR_VALUE, PARAMETER, PROD_NAME_STR, DESCRIPTION, DISPO, EWSD, SDH, IPS, SCT, CHECK_QUANTITY, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS)
values (115, 'Installation Kunden-PC', null, 10100, 'SERVICE', null, null, null, ' ', 'Service-Leistung Installation des Kunden-PC', '0', '0', '0', '0', '0', '0', '1', '2009-06-01', '2200-01-01');

drop sequence S_T_PROD_2_TECH_LEISTUNG_0;
create sequence S_T_PROD_2_TECH_LEISTUNG_0 start with 600;
grant select on S_T_PROD_2_TECH_LEISTUNG_0 to public;

insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (550, 511, 111, null, 1);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (551, 512, 23, null, 0);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (552, 512, 24, null, 0);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (553, 512, 25, null, 0);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (554, 512, 114, 23, 1);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (555, 512, 27, 24, 1);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (556, 512, 28, 25, 1);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (557, 512, 111, null, 0);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (558, 512, 3, null, 0);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (560, 513, 23, null, 0);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (561, 513, 24, null, 0);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (562, 513, 25, null, 0);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (563, 513, 114, 23, 1);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (564, 513, 27, 24, 1);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (565, 513, 28, 25, 1);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (566, 513, 111, null, 0);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (567, 513, 113, null, 0);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (568, 513, 3, null, 0);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (569, 512, 115, null, 0);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (570, 513, 115, null, 0);

-- Physiktyp
insert into T_PRODUKT_2_PHYSIKTYP (ID, PROD_ID, PHYSIKTYP, PHYSIKTYP_ADDITIONAL, VIRTUELL, PARENTPHYSIKTYP_ID)
values (310, 511, 801, null, 0, null);
insert into T_PRODUKT_2_PHYSIKTYP (ID, PROD_ID, PHYSIKTYP, PHYSIKTYP_ADDITIONAL, VIRTUELL, PARENTPHYSIKTYP_ID)
values (311, 512, 800, null, 0, null);
insert into T_PRODUKT_2_PHYSIKTYP (ID, PROD_ID, PHYSIKTYP, PHYSIKTYP_ADDITIONAL, VIRTUELL, PARENTPHYSIKTYP_ID)
values (312, 513, 800, null, 0, null);

-- Rufnummern-Leistung
-- Neues Leistungsbündel
insert into T_LEISTUNGSBUENDEL (ID, NAME, BESCHREIBUNG)
values (13, 'Maxi Deluxe VoIP', 'Leistungsbündel für Maxi-Deluxe Produkte');

drop sequence S_T_LB_2_LEISTUNG_0;
create sequence S_T_LB_2_LEISTUNG_0 start with 420;
grant select on S_T_LB_2_LEISTUNG_0 to public;

insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (385, 13, 1, 60, '1', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (386, 13, 5, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (387, 13, 7, 60, '1', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (388, 13, 11, 60, '1', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (389, 13, 13, 60, '1', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (390, 13, 19, 60, '1', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (391, 13, 26, 60, '1', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (392, 13, 28, 60, '1', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', '25');
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (393, 13, 29, 60, '1', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (394, 13, 30, 60, '1', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (395, 13, 46, 60, '1', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (396, 13, 31, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (397, 13, 32, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (398, 13, 33, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (399, 13, 34, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (400, 13, 35, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (401, 13, 36, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (402, 13, 37, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (403, 13, 38, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (404, 13, 39, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (405, 13, 40, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (406, 13, 41, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (407, 13, 42, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (408, 13, 43, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (409, 13, 44, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (410, 13, 46, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (411, 13, 2, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (412, 13, 6, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (413, 13, 14, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (414, 13, 58, 60, '1', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', '1');
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (415, 13, 59, 60, '1', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (416, 13, 4, 60, '0', '2009-06-01', '2200-01-01', '2009-06-01', '2200-01-01', NULL);

insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION)
values (13, 42408, null, 2011, 'Maxi Telefon');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION)
values (13, 42510, null, 2012, 'Maxi Pur deluxe');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION)
values (13, 42550, null, 2014, 'Maxi Komplett deluxe');
--insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION)
--values (12, 42551, null, 2014, 'Maxi Komplett deluxe');



-- Produkt-Mapping
insert into T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE)
values (1030, 511, 511, 'phone');
insert into T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE)
values (1031, 512, 512, 'phone');
insert into T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE)
values (1032, 513, 513, 'phone');


-- ServiceChain
insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
values (2014, 'verl.check.physik', 'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckPhysik4DeluxeCommand',
    'VERLAUF_CHECK', 'Command prueft die Physik-Zuordnung zum Auftrags, speziell bei Maxi Deluxe');
insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
    values (2015, 'verl.check.auftrag.zeitfenster',
    'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckZeitfensterCommand',
    'VERLAUF_CHECK', 'Command prueft die Zeitfenster-Zuordnung zum Auftrag');
insert into T_SERVICE_CHAIN (ID, NAME, TYPE, DESCRIPTION)
values (45, 'VOIP With Account and ShortTerm-Check', 'VERLAUF_CHECK', 'Verlauf-Chain fuer Maxi Pur deluxe und Maxi Komplett deluxe');
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
values (905, 2013, 45, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
values (906, 2000, 45, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 1);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
values (907, 2010, 45, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 2);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
values (908, 2004, 45, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 3);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
values (909, 2008, 45, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 4);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
values (910, 2014, 45, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 5);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
values (911, 2015, 45, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 6);

-- DSLAM Profile
insert into T_DSLAM_PROFILE (ID, NAME, BEMERKUNG, DOWNSTREAM, UPSTREAM, FASTPATH, UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS, FASTPATH_TECH_LS)
values (85, 'MD_25000_2500_H', null, '25000', '2500', '0', null, 23, 114, null);
insert into T_DSLAM_PROFILE (ID, NAME, BEMERKUNG, DOWNSTREAM, UPSTREAM, FASTPATH, UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS, FASTPATH_TECH_LS)
values (86, 'MD_50000_5000_H', null, '50000', '5000', '0', null, 24, 27, null);
insert into T_DSLAM_PROFILE (ID, NAME, BEMERKUNG, DOWNSTREAM, UPSTREAM, FASTPATH, UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS, FASTPATH_TECH_LS)
values (87, 'MD_100000_10000_H', null, '100000', '10000', '0', null, 25, 28, null);

insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID) values (512, 85);
insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID) values (512, 86);
insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID) values (512, 87);
insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID) values (513, 85);
insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID) values (513, 86);
insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID) values (513, 87);

insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
values (1009, 'add.voip.deluxe', 'de.augustakom.hurrican.service.cc.impl.command.leistung.CreateVoIPDeluxeCommand', 'LS_ZUGANG', 'Command-Klasse, um notwendige Daten fuer VoIP bei Maxi-Deluxe zu erzeugen');
update T_SERVICE_COMMAND_MAPPING set COMMAND_ID = 1009 where REF_ID = 111 and REF_CLASS = 'de.augustakom.hurrican.model.cc.TechLeistung';
update T_SERVICE_COMMAND_MAPPING set COMMAND_ID = 1009 where REF_ID = 112 and REF_CLASS = 'de.augustakom.hurrican.model.cc.TechLeistung';

-- Produktkonfiguration anpassen
ALTER TABLE T_PRODUKT ADD STATUSMELDUNGEN char(1);
update T_PRODUKT SET STATUSMELDUNGEN = 1 where prod_id = 511;
update T_PRODUKT SET STATUSMELDUNGEN = 1 where prod_id = 512;
update T_PRODUKT SET STATUSMELDUNGEN = 1 where prod_id = 513;
-- Zugriffsdaten fuer Taifun WebService definieren
-- User zur WebService Authentifizierung: beliebige eMail aus Tabelle CUSTOMER !!!
--    TODO: einen Dummy-Kunden unter M-net (1..9) anlegen und dessen eMail verwenden!
--          (z.B. HURRICAN_WEBSERVICE mit hurrican@m-net.de)
insert into T_WEBSERVICE_CONFIG (ID, NAME, DEST_SYSTEM, WS_URL, WS_SEC_ACTION, WS_SEC_USER, WS_SEC_PASSWORD,
    APP_SEC_USER, APP_SEC_PASSWORD, DESCRIPTION)
    values (3, 'Taifun WebService', 'TAIFUN', 'http://mnettaifun02:8080/mnet/ws', null, null, null,
    'j.glink@augustakom.de', '6HTp3Lb4aYKQ=9e',
    'WebService des CPS SourceAgents; Asynchroner Modus');

-- Counter einfuegen
insert into T_COUNTER (COUNTER, INTVALUE) values ('deluxe.asb', 100);

commit;


-- Taifun ToDO
update LEISTUNG set EXT_PRODUKT__NO = 511 where LEISTUNG__NO = 42408;
update LEISTUNG set EXT_PRODUKT__NO = 512 where LEISTUNG__NO = 42510;
update LEISTUNG set EXT_PRODUKT__NO = 513 where LEISTUNG__NO = 42550;
update LEISTUNG set EXT_PRODUKT__NO = null, EXT_LEISTUNG__NO = 20013 where LEISTUNG__NO = 42551;
update LEISTUNG set EXT_LEISTUNG__NO = 10025 where LEISTUNG__NO = 42531;
update LEISTUNG set EXT_LEISTUNG__NO = 10026 where LEISTUNG__NO = 42532;
update LEISTUNG set EXT_LEISTUNG__NO = 10026 where LEISTUNG__NO = 42488;
update LEISTUNG set EXT_LEISTUNG__NO = 10027 where LEISTUNG__NO = 42489;
update LEISTUNG set EXT_LEISTUNG__NO = null where LEISTUNG__NO = 39213;
update LEISTUNG set EXT_LEISTUNG__NO = null where LEISTUNG__NO = 42490;

--TODO ->im JIRA eingestellt
GRANT select, insert, update ON ACCOUNT TO "R_BILLING_READER";
GRANT SELECT ON TIME_SLOT TO "R_MNET_READ_ONLY";
GRANT SELECT ON TIME_SLOT_CONFIG TO "R_MNET_READ_ONLY";

insert into TIME_SLOT_CONFIG (TIME_SLOT_CONFIG_NO, VALID_FROM, VALID_TO, DAYTIME_FROM, DAYTIME_TO, WEEKDAY, AREA_NO, MAX_FREE_SLOTS, USERW, DATEW)
values (1, to_date('01.09.2009','dd.mm.yyyy'), to_date('31.12.9999','dd.mm.yyyy'), to_date('07:00','HH:MI') , to_date('10:00','HH:MI'), 1, 2, 99, 'gilgan', to_date('24.09.2009','dd.mm.yyyy'));
insert into TIME_SLOT_CONFIG (TIME_SLOT_CONFIG_NO, VALID_FROM, VALID_TO, DAYTIME_FROM, DAYTIME_TO, WEEKDAY, AREA_NO, MAX_FREE_SLOTS, USERW, DATEW)
values (2, to_date('01.09.2009','dd.mm.yyyy'), to_date('31.12.9999','dd.mm.yyyy'), to_date('07:00','HH:MI') , to_date('10:00','HH:MI'), 2, 2, 99, 'gilgan', to_date('24.09.2009','dd.mm.yyyy'));
insert into TIME_SLOT_CONFIG (TIME_SLOT_CONFIG_NO, VALID_FROM, VALID_TO, DAYTIME_FROM, DAYTIME_TO, WEEKDAY, AREA_NO, MAX_FREE_SLOTS, USERW, DATEW)
values (3, to_date('01.09.2009','dd.mm.yyyy'), to_date('31.12.9999','dd.mm.yyyy'), to_date('07:00','HH:MI') , to_date('10:00','HH:MI'), 3, 2, 99, 'gilgan', to_date('24.09.2009','dd.mm.yyyy'));
insert into TIME_SLOT_CONFIG (TIME_SLOT_CONFIG_NO, VALID_FROM, VALID_TO, DAYTIME_FROM, DAYTIME_TO, WEEKDAY, AREA_NO, MAX_FREE_SLOTS, USERW, DATEW)
values (4, to_date('01.09.2009','dd.mm.yyyy'), to_date('31.12.9999','dd.mm.yyyy'), to_date('07:00','HH:MI') , to_date('10:00','HH:MI'), 4, 2, 99, 'gilgan', to_date('24.09.2009','dd.mm.yyyy'));
insert into TIME_SLOT_CONFIG (TIME_SLOT_CONFIG_NO, VALID_FROM, VALID_TO, DAYTIME_FROM, DAYTIME_TO, WEEKDAY, AREA_NO, MAX_FREE_SLOTS, USERW, DATEW)
values (5, to_date('01.09.2009','dd.mm.yyyy'), to_date('31.12.9999','dd.mm.yyyy'), to_date('07:00','HH:MI') , to_date('10:00','HH:MI'), 5, 2, 99, 'gilgan', to_date('24.09.2009','dd.mm.yyyy'));
insert into TIME_SLOT_CONFIG (TIME_SLOT_CONFIG_NO, VALID_FROM, VALID_TO, DAYTIME_FROM, DAYTIME_TO, WEEKDAY, AREA_NO, MAX_FREE_SLOTS, USERW, DATEW)
values (6, to_date('01.09.2009','dd.mm.yyyy'), to_date('31.12.9999','dd.mm.yyyy'), to_date('08:00','HH:MI') , to_date('12:00','HH:MI'), 1, 2, 99, 'gilgan', to_date('24.09.2009','dd.mm.yyyy'));
insert into TIME_SLOT_CONFIG (TIME_SLOT_CONFIG_NO, VALID_FROM, VALID_TO, DAYTIME_FROM, DAYTIME_TO, WEEKDAY, AREA_NO, MAX_FREE_SLOTS, USERW, DATEW)
values (7, to_date('01.09.2009','dd.mm.yyyy'), to_date('31.12.9999','dd.mm.yyyy'), to_date('08:00','HH:MI') , to_date('12:00','HH:MI'), 2, 2, 99, 'gilgan', to_date('24.09.2009','dd.mm.yyyy'));
insert into TIME_SLOT_CONFIG (TIME_SLOT_CONFIG_NO, VALID_FROM, VALID_TO, DAYTIME_FROM, DAYTIME_TO, WEEKDAY, AREA_NO, MAX_FREE_SLOTS, USERW, DATEW)
values (8, to_date('01.09.2009','dd.mm.yyyy'), to_date('31.12.9999','dd.mm.yyyy'), to_date('08:00','HH:MI') , to_date('12:00','HH:MI'), 3, 2, 99, 'gilgan', to_date('24.09.2009','dd.mm.yyyy'));
insert into TIME_SLOT_CONFIG (TIME_SLOT_CONFIG_NO, VALID_FROM, VALID_TO, DAYTIME_FROM, DAYTIME_TO, WEEKDAY, AREA_NO, MAX_FREE_SLOTS, USERW, DATEW)
values (9, to_date('01.09.2009','dd.mm.yyyy'), to_date('31.12.9999','dd.mm.yyyy'), to_date('08:00','HH:MI') , to_date('12:00','HH:MI'), 4, 2, 99, 'gilgan', to_date('24.09.2009','dd.mm.yyyy'));
insert into TIME_SLOT_CONFIG (TIME_SLOT_CONFIG_NO, VALID_FROM, VALID_TO, DAYTIME_FROM, DAYTIME_TO, WEEKDAY, AREA_NO, MAX_FREE_SLOTS, USERW, DATEW)
values (10, to_date('01.09.2009','dd.mm.yyyy'), to_date('31.12.9999','dd.mm.yyyy'), to_date('08:00','HH:MI') , to_date('12:00','HH:MI'), 5, 2, 99, 'gilgan', to_date('24.09.2009','dd.mm.yyyy'));
insert into TIME_SLOT_CONFIG (TIME_SLOT_CONFIG_NO, VALID_FROM, VALID_TO, DAYTIME_FROM, DAYTIME_TO, WEEKDAY, AREA_NO, MAX_FREE_SLOTS, USERW, DATEW)
values (11, to_date('01.09.2009','dd.mm.yyyy'), to_date('31.12.9999','dd.mm.yyyy'), to_date('13:00','HH24:MI') , to_date('17:00','HH24:MI'), 1, 2, 99, 'gilgan', to_date('24.09.2009','dd.mm.yyyy'));
insert into TIME_SLOT_CONFIG (TIME_SLOT_CONFIG_NO, VALID_FROM, VALID_TO, DAYTIME_FROM, DAYTIME_TO, WEEKDAY, AREA_NO, MAX_FREE_SLOTS, USERW, DATEW)
values (12, to_date('01.09.2009','dd.mm.yyyy'), to_date('31.12.9999','dd.mm.yyyy'), to_date('13:00','HH24:MI') , to_date('17:00','HH24:MI'), 2, 2, 99, 'gilgan', to_date('24.09.2009','dd.mm.yyyy'));
insert into TIME_SLOT_CONFIG (TIME_SLOT_CONFIG_NO, VALID_FROM, VALID_TO, DAYTIME_FROM, DAYTIME_TO, WEEKDAY, AREA_NO, MAX_FREE_SLOTS, USERW, DATEW)
values (13, to_date('01.09.2009','dd.mm.yyyy'), to_date('31.12.9999','dd.mm.yyyy'), to_date('13:00','HH24:MI') , to_date('17:00','HH24:MI'), 3, 2, 99, 'gilgan', to_date('24.09.2009','dd.mm.yyyy'));
insert into TIME_SLOT_CONFIG (TIME_SLOT_CONFIG_NO, VALID_FROM, VALID_TO, DAYTIME_FROM, DAYTIME_TO, WEEKDAY, AREA_NO, MAX_FREE_SLOTS, USERW, DATEW)
values (14, to_date('01.09.2009','dd.mm.yyyy'), to_date('31.12.9999','dd.mm.yyyy'), to_date('13:00','HH24:MI') , to_date('17:00','HH24:MI'), 4, 2, 99, 'gilgan', to_date('24.09.2009','dd.mm.yyyy'));
insert into TIME_SLOT_CONFIG (TIME_SLOT_CONFIG_NO, VALID_FROM, VALID_TO, DAYTIME_FROM, DAYTIME_TO, WEEKDAY, AREA_NO, MAX_FREE_SLOTS, USERW, DATEW)
values (15, to_date('01.09.2009','dd.mm.yyyy'), to_date('31.12.9999','dd.mm.yyyy'), to_date('13:00','HH24:MI') , to_date('17:00','HH24:MI'), 5, 2, 99, 'gilgan', to_date('24.09.2009','dd.mm.yyyy'));
insert into TIME_SLOT_CONFIG (TIME_SLOT_CONFIG_NO, VALID_FROM, VALID_TO, DAYTIME_FROM, DAYTIME_TO, WEEKDAY, AREA_NO, MAX_FREE_SLOTS, USERW, DATEW)
values (16, to_date('01.09.2009','dd.mm.yyyy'), to_date('31.12.9999','dd.mm.yyyy'), to_date('16:00','HH24:MI') , to_date('19:00','HH24:MI'), 2, 2, 99, 'gilgan', to_date('24.09.2009','dd.mm.yyyy'));
insert into TIME_SLOT_CONFIG (TIME_SLOT_CONFIG_NO, VALID_FROM, VALID_TO, DAYTIME_FROM, DAYTIME_TO, WEEKDAY, AREA_NO, MAX_FREE_SLOTS, USERW, DATEW)
values (17, to_date('01.09.2009','dd.mm.yyyy'), to_date('31.12.9999','dd.mm.yyyy'), to_date('16:00','HH24:MI') , to_date('19:00','HH24:MI'), 4, 2, 99, 'gilgan', to_date('24.09.2009','dd.mm.yyyy'));



insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, FLOAT_VALUE, UNIT_ID, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
values (11011, 'STANDORT_TYP', 'FTTH', null, null, 11100, 1, null, 'Standort-Typ FTTH');

insert into T_HW_BAUGRUPPEN_TYP (ID, NAME, PORT_COUNT, DESCRIPTION, IS_ACTIVE, HW_SCHNITTSTELLE_NAME, HW_TYPE_NAME)
values (53, 'OLT', 0, 'OLT', 1, null, null);

insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, INVENTAR_NR, MOD_NUMBER, SUBRACK_ID)
values (8088, 950, 53, 1, null, '00-00', null);
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, INVENTAR_NR, MOD_NUMBER, SUBRACK_ID)
values (8089, 951, 53, 1, null, '00-00', null);
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, INVENTAR_NR, MOD_NUMBER, SUBRACK_ID)
values (8090, 952, 53, 1, null, '00-00', null);
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, INVENTAR_NR, MOD_NUMBER, SUBRACK_ID)
values (8090, 952, 53, 1, null, '00-00', null);
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, INVENTAR_NR, MOD_NUMBER, SUBRACK_ID)
values (8122, 970, 53, 1, null, '00-00', null);
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, INVENTAR_NR, MOD_NUMBER, SUBRACK_ID)
values (8123, 971, 53, 1, null, '00-00', null);

GRANT SELECT ON DEVICE TO "R_MNET_READ_ONLY";
GRANT SELECT ON DEVICE__FRITZBOX TO "R_MNET_READ_ONLY";
GRANT SELECT ON DEVICE2SERVICE TO "R_MNET_READ_ONLY";
GRANT SELECT ON MACADDR TO "R_MNET_READ_ONLY";

-- pruefen
GRANT SELECT ON DEVICETYPE TO "R_MNET_READ_ONLY";