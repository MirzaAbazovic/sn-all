--
-- Sample-Views fuer BSI
--

-- View, um die technischen Auftraege inkl. Status zu einem Taifun-Auftrag darzustellen.
--  enthaltene Daten:
--    - Hurrican Auftrags-ID
--    - technische Produktauspraegung
--    - Vorgabe-Datum (AM), Inbetriebnahmedatum, Kuendigungsdatum
--    - Status des techn. Auftrags
--    - Leitungsnummer zu dem Auftrag
--    - Account (z.B. DSL-Account)
create or replace view HURRICAN_TECH_ORDER_DETAILS as 
 (select ad.PRODAK_ORDER__NO as ORDER__NO, a.id as HURRICAN_ID, p.ANSCHLUSSART as TECH_PRODUKT, 
 	ad.VORGABE_SCV as VORGABE_AM, ad.INBETRIEBNAHME as INBETRIEBNAHME, ad.KUENDIGUNG as KUENDIGUNG, 
	ast.STATUS_TEXT as TECH_STATUS, tdn.TDN as LEITUNGSNUMMER, acc.ACCOUNT as ACCOUNT
	from t_auftrag a
	inner join t_auftrag_daten ad on a.id=ad.auftrag_id
	inner join t_auftrag_status ast on ad.status_id=ast.id
	inner join t_auftrag_technik at on a.id=at.auftrag_id
	inner join t_produkt p on ad.prod_id=p.prod_id
	left join t_tdn tdn on at.tdn_id=tdn.id
	left join t_int_account acc on at.int_account_id=acc.id
	where ad.gueltig_bis>=SYSDATE
	and at.gueltig_bis>=SYSDATE
);


-- View, um die DSLAM-Profile zu einem Taifun-Auftrag zu ermitteln.
-- Pro Taifun-Auftrag kann es mehrere DSLAM-Profile geben, wobei immer nur ein Profil "aktiv" ist.
-- Zusaetzlich wird noch pro Profil der technische Auftrag inkl. Status eingeblendet.
-- (Es handelt sich dabei um das in der DB vorgesehene DSLAM-Profil. Theoretisch kann jedoch
-- ein anderes Profil aktiviert sein. Dies ist nur ueber eine Live-Abfrage vom DSLAM zu ermitteln.)
create or replace view HURRICAN_ORDER2DSLAMPROFILE as 
 (select ad.PRODAK_ORDER__NO as ORDER__NO, ad.AUFTRAG_ID as HURRICAN_ID, ast.STATUS_TEXT as TECH_STATUS, 
	d.NAME as DSLAM_PROFILE, a2d.GUELTIG_VON as VON, a2d.GUELTIG_BIS as BIS, a2d.USERW as BENUTZER, 
	a2d.BEMERKUNG as BEMERKUNG, r.NAME as CHANGE_REASON
	from t_auftrag_daten ad
	inner join t_auftrag_status ast on ad.status_id=ast.id
	left join t_auftrag_2_dslamprofile a2d on ad.auftrag_id=a2d.auftrag_id
	left join t_dslam_profile d on a2d.dslam_profile_id=d.id
	left join t_dslam_profile_change_reason r on a2d.change_reason_id=r.id
	where ad.gueltig_bis>=SYSDATE
);


-- View, um die bei einem Carrier bestellten Port-Daten zu einem Taifun-Auftrag zu erhalten
-- (keine Details der eigentlichen Carrierbestellung!)
create or replace view HURRICAN_ORDER2EQ as 
  (select ad.prodak_order__no as ORDER__NO, eq.CARRIER as CARRIER, eq.HW_EQN as EQN, eq.RANG_VERTEILER as VERTEILER, 
    eq.RANG_REIHE as REIHE, eq.RANG_BUCHT as BUCHT, eq.RANG_LEISTE1 as LEISTE1, 
    eq.RANG_STIFT1 as STIFT1, eq.RANG_LEISTE2 as LEISTE2, eq.RANG_STIFT2 as STIFT2, 
    eq.UETV as UETV, hg.ORTSTEIL as HVT,
    hs.ASB as ASB
	from t_auftrag_daten ad inner join
	t_auftrag_technik at on ad.auftrag_id = at.auftrag_id 
	inner join t_endstelle e on at.at_2_es_id = e.es_gruppe 
	left join t_rangierung r on e.rangier_id = r.rangier_id 
	left join t_equipment eq on r.eq_out_id = eq.eq_id
	left join t_hvt_standort hs on eq.HVT_ID_STANDORT=hs.HVT_ID_STANDORT
	left join t_hvt_gruppe hg on hs.HVT_GRUPPE_ID=hg.HVT_GRUPPE_ID
	where ad.gueltig_bis >= SYSDATE
	and at.gueltig_bis >= SYSDATE
	and r.gueltig_bis >= SYSDATE
	and e.ES_TYP='B'
);







