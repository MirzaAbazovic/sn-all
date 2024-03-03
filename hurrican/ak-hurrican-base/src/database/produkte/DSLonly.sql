--
-- SQL-Script, um das Produkt 'DSLonly' in Hurrican anzulegen.
--
-- in PROD ausgefuehrt am 29.03.2007
--

INSERT INTO T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, LEITUNGSART, PROD_NAME_PATTERN, AKTIONS_ID,
	BRAUCHT_DN, AUFTRAGSERSTELLUNG, LTGNR_VORS, LTGNR_ANLEGEN, BRAUCHT_BUENDEL, 
	ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, ACCOUNT_VORS, LI_NR, VPN_PHYSIK, IS_PARENT,
	CHECK_CHILD, VERTEILUNG_DURCH, EXPORT_KDP_M, AUTO_PRODUCT_CHANGE, IS_COMBI_PRODUKT, GUELTIG_VON, GUELTIG_BIS) 
	VALUES (440, 17, '', 'Maxi Pur', null, 'Maxi Pur {DOWNSTREAM}', 1, 
	0, 0, 'DV', 1, 0, 
	1, 0, 1, 'as', 1, 1, 0, 
	0, 4, 1, 0, 0, '2007-03-28', '2200-01-01');
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (440, 1),(440,2);
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (440, 32);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (440,2);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (440,514);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) VALUES (315,440,'de.augustakom.hurrican.model.cc.Produkt');
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE) VALUES (1009, 440, 440, 'dsl');

-- moegliche techn. Leistungen zuordnen
-- Fastpath
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (440, 1, null, 0);
-- 512 kbit/s Upstream (Default)
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (440, 14, 11, 1);
-- always-on
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (440, 3, null, 0);
-- feste IP-Adresse
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (440, 4, null, 0);
-- Downstreams 6000 und 16000
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (440, 11, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (440, 12, null, 0);
-- Upstream 800 kbit/s (bei 16000 Downstream)
INSERT INTO T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (440, 15, 12, 1);

-- Endgeraete anlegen und zuordnen
INSERT INTO T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) VALUES (440, 11, 1);

-- Strassen/Produktmapping erstellen (Basisprodukt: 326 - MaxiDSL6000)
create table p2sl_tmp (
	prod_id integer(9) not null,
	sl_id integer(9) not null,
	freigabe_id integer(9) not null);
insert into p2sl_tmp (prod_id, sl_id, freigabe_id)
	select 440, src.sl_id, src.freigabe_id from t_prod_2_sl src where src.prod_id=326;
insert into t_prod_2_sl (prod_id, sl_id, freigabe_id) 
	select src.prod_id, src.sl_id, src.freigabe_id from p2sl_tmp src;
delete from p2sl_tmp;
drop table p2sl_tmp;


-- TODO: Rangierungsmatrix fuer alle HVTs ueber GUI erstellen


