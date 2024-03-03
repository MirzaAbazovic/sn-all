--
-- SQL-Script fuer die Einrichtung von SDSLvario/SDSLflat
--

-- Leitungsart 4600 kbit/s anlegen
insert into t_leitungsart (id, name) values (69, '4600 kbit/s');

-- SDSL 256
INSERT INTO T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, LEITUNGSART, UPSTREAM, AKTIONS_ID,
	BRAUCHT_DN, AUFTRAGSERSTELLUNG, LTGNR_VORS, LTGNR_ANLEGEN, BRAUCHT_BUENDEL, PROD_ID_TEXT,
	ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, ACCOUNT_VORS, LI_NR, VPN_PHYSIK, IS_PARENT,
	CHECK_CHILD, VERTEILUNG_DURCH, EXPORT_KDP_M, BRAUCHT_VPI_VCI, GUELTIG_VON, GUELTIG_BIS) 
	VALUES (60, 4, null, 'SDSL 256', 25, 25, 1, 0, 0, 'DV', 1, 0, '60', 
	1, 0, 1, 'sd', 0, 1, 1, 0, 4, 1, 1, '2006-05-01', '2200-01-01');
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (60,19);
INSERT INTO T_BA_VERL_NEU (PROD_ID, EWSD, SDH, SCT, IPS, AUSBLENDEN) VALUES (60,0,1,1,1,0);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (60, 1);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (60, 2);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (60,9);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (60,2);
-- SDSL 512
INSERT INTO T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, LEITUNGSART, UPSTREAM, AKTIONS_ID,
	BRAUCHT_DN, AUFTRAGSERSTELLUNG, LTGNR_VORS, LTGNR_ANLEGEN, BRAUCHT_BUENDEL, PROD_ID_TEXT,
	ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, ACCOUNT_VORS, LI_NR, VPN_PHYSIK, IS_PARENT,
	CHECK_CHILD, VERTEILUNG_DURCH, EXPORT_KDP_M, BRAUCHT_VPI_VCI, GUELTIG_VON, GUELTIG_BIS) 
	VALUES (61, 4, null, 'SDSL 512', 37, 37, 1, 0, 0, 'DV', 1, 0, '61', 
	1, 0, 1, 'sd', 0, 1, 1, 0, 4, 1, 1, '2006-05-01', '2200-01-01');
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (61,19);
INSERT INTO T_BA_VERL_NEU (PROD_ID, EWSD, SDH, SCT, IPS, AUSBLENDEN) VALUES (61,0,1,1,1,0);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (61, 1);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (61, 2);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (61,9);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (61,2);
-- SDSL 1024
INSERT INTO T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, LEITUNGSART, UPSTREAM, AKTIONS_ID,
	BRAUCHT_DN, AUFTRAGSERSTELLUNG, LTGNR_VORS, LTGNR_ANLEGEN, BRAUCHT_BUENDEL, PROD_ID_TEXT,
	ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, ACCOUNT_VORS, LI_NR, VPN_PHYSIK, IS_PARENT,
	CHECK_CHILD, VERTEILUNG_DURCH, EXPORT_KDP_M, BRAUCHT_VPI_VCI, GUELTIG_VON, GUELTIG_BIS) 
	VALUES (62, 4, null, 'SDSL 1024', 6, 6, 1, 0, 0, 'DV', 1, 0, '62', 
	1, 0, 1, 'sd', 0, 1, 1, 0, 4, 1, 1, '2006-05-01', '2200-01-01');
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (62,19);
INSERT INTO T_BA_VERL_NEU (PROD_ID, EWSD, SDH, SCT, IPS, AUSBLENDEN) VALUES (62,0,1,1,1,0);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (62, 1);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (62, 2);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (62,9);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (62,2);
-- SDSL 2300
INSERT INTO T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, LEITUNGSART, UPSTREAM, AKTIONS_ID,
	BRAUCHT_DN, AUFTRAGSERSTELLUNG, LTGNR_VORS, LTGNR_ANLEGEN, BRAUCHT_BUENDEL, PROD_ID_TEXT,
	ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, ACCOUNT_VORS, LI_NR, VPN_PHYSIK, IS_PARENT,
	CHECK_CHILD, VERTEILUNG_DURCH, EXPORT_KDP_M, BRAUCHT_VPI_VCI, GUELTIG_VON, GUELTIG_BIS) 
	VALUES (63, 4, null, 'SDSL 2300', 24, 24, 1, 0, 0, 'DV', 1, 0, '63', 
	1, 0, 1, 'sd', 0, 1, 1, 0, 4, 1, 1, '2006-05-01', '2200-01-01');
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (63,19);
INSERT INTO T_BA_VERL_NEU (PROD_ID, EWSD, SDH, SCT, IPS, AUSBLENDEN) VALUES (63,0,1,1,1,0);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (63, 1);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (63, 2);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (63,9);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (63,2);
-- SDSL 4600
INSERT INTO T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, LEITUNGSART, UPSTREAM, AKTIONS_ID,
	BRAUCHT_DN, AUFTRAGSERSTELLUNG, LTGNR_VORS, LTGNR_ANLEGEN, BRAUCHT_BUENDEL, PROD_ID_TEXT,
	ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, ACCOUNT_VORS, LI_NR, VPN_PHYSIK, IS_PARENT,
	CHECK_CHILD, VERTEILUNG_DURCH, EXPORT_KDP_M, BRAUCHT_VPI_VCI, GUELTIG_VON, GUELTIG_BIS) 
	VALUES (64, 4, null, 'SDSL 4600', 69, 69, 1, 0, 0, 'DV', 1, 0, '64', 
	1, 0, 1, 'sd', 0, 1, 1, 0, 4, 1, 1, '2006-05-01', '2200-01-01');
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (64,19);
INSERT INTO T_BA_VERL_NEU (PROD_ID, EWSD, SDH, SCT, IPS, AUSBLENDEN) VALUES (64,0,1,1,1,0);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (64, 1);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (64, 2);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (64,9);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (64,2);


-- Produktaenderungen konfigurieren
-- TODO
-- koennen erst konfiguriert werden, wenn alle techn. Details (z.B. Accounttyp) fuer
-- den neuen SDSL geklaert sind. Evtl. muessen dann neue Service-Chains erstellt werden.

-- Strassenkonfiguration
  -- temp. Tabelle anlegen
create table p2sl_tmp (
	prod_id integer(9) not null,
	sl_id integer(9) not null,
	freigabe_id integer(9) not null);
  -- Konfiguration fuer SDSL 256
insert into p2sl_tmp (prod_id, sl_id, freigabe_id)
	select 60, src.sl_id, src.freigabe_id from t_prod_2_sl src where src.prod_id=11;
insert into t_prod_2_sl (prod_id, sl_id, freigabe_id) 
	select src.prod_id, src.sl_id, src.freigabe_id from p2sl_tmp src;
delete from p2sl_tmp;
  -- Konfiguration fuer SDSL 512
insert into p2sl_tmp (prod_id, sl_id, freigabe_id)
	select 61, src.sl_id, src.freigabe_id from t_prod_2_sl src where src.prod_id=17;
insert into t_prod_2_sl (prod_id, sl_id, freigabe_id) 
	select src.prod_id, src.sl_id, src.freigabe_id from p2sl_tmp src;
delete from p2sl_tmp;
  -- Konfiguration fuer SDSL 1024
insert into p2sl_tmp (prod_id, sl_id, freigabe_id)
	select 62, src.sl_id, src.freigabe_id from t_prod_2_sl src where src.prod_id=16;
insert into t_prod_2_sl (prod_id, sl_id, freigabe_id) 
	select src.prod_id, src.sl_id, src.freigabe_id from p2sl_tmp src;
delete from p2sl_tmp;
  -- Konfiguration fuer SDSL 2300
insert into p2sl_tmp (prod_id, sl_id, freigabe_id)
	select 63, src.sl_id, src.freigabe_id from t_prod_2_sl src where src.prod_id=18;
insert into t_prod_2_sl (prod_id, sl_id, freigabe_id) 
	select src.prod_id, src.sl_id, src.freigabe_id from p2sl_tmp src;
delete from p2sl_tmp;
  -- Konfiguration fuer SDSL 4600
insert into p2sl_tmp (prod_id, sl_id, freigabe_id)
	select 64, src.sl_id, src.freigabe_id from t_prod_2_sl src where src.prod_id=18;
insert into t_prod_2_sl (prod_id, sl_id, freigabe_id) 
	select src.prod_id, src.sl_id, src.freigabe_id from p2sl_tmp src;
delete from p2sl_tmp;
  -- temp. Tabelle wieder entfernen
drop table p2sl_tmp;


-- TODO: Rangierungsmatrix ueber GUI erstellen


-- TODO: in Methode IntAccountDAOImpl.findAuftragAccountViews(..) muessen die neuen Produkt-IDs
-- evtl. mit aufgenommen werden (falls Abrechnungsaccount verwendet wird).

--
-- zusaetzlich muss Script update4SDSL4Draht.sql ausgefuehrt werden!
--

