--
-- Update-Script fuer den 4-Draht SDSL
--

-- Zusatz-Produkt fuer 4-Draht SDSL anlegen
INSERT INTO T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, LEITUNGSART, UPSTREAM, AKTIONS_ID,
	BRAUCHT_DN, AUFTRAGSERSTELLUNG, LTGNR_VORS, LTGNR_ANLEGEN, BRAUCHT_BUENDEL, PROD_ID_TEXT,
	ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, ACCOUNT_VORS, LI_NR, VPN_PHYSIK, IS_PARENT,
	CHECK_CHILD, VERTEILUNG_DURCH, GUELTIG_VON, GUELTIG_BIS) 
	VALUES (99, 4, null, 'SDSL 4-Draht Option', null, null, 1, 0, 0, 'DV', 1, 0, 
	'99', 1, 0, 1, null, 2, 1, 0, 0, 4, '2006-04-01', '2200-01-01');

INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (99, 1);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (99, 2);

INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (99, 10);

-- Verlaufs-Konfiguration
insert into T_BA_VERL_NEU (PROD_ID, EWSD, SDH, SCT, IPS, AUSBLENDEN) VALUES (99,0,1,1,0,0);


-- Produkt/Strassenzuordnung identisch mit SDSL 512
create table p2sl_tmp (
	prod_id integer(9) not null,
	sl_id integer(9) not null,
	freigabe_id integer(9) not null);
-- Konfiguration fuer DSL2000
insert into p2sl_tmp (prod_id, sl_id, freigabe_id)
	select 99, src.sl_id, src.freigabe_id from t_prod_2_sl src where src.prod_id=17;
insert into t_prod_2_sl (prod_id, sl_id, freigabe_id) 
	select src.prod_id, src.sl_id, src.freigabe_id from p2sl_tmp src;
delete from p2sl_tmp;
-- temp. Tabelle wieder entfernen
drop table p2sl_tmp;


-- Physikaenderung
insert into T_PHYSIKAENDERUNGSTYP (ID, NAME, SDH, SCT) VALUES (5007, 'Wandel SDSL 2- auf 4-Draht', 1, 1);


