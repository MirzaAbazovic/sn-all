--
-- Endgeraete neu konfigurieren
--

-- zusaetzliche Endgeraete aufnehmen
insert into T_EG (ID, INTERNE__ID, NAME, BESCHREIBUNG, TYP, VERFUEGBAR_VON, VERFUEGBAR_BIS, CONFIGURABLE)
	values (10, 10, 'NT', 'NT fuer ISDN-Anschluesse', 4, '2006-08-01', '2200-01-01', 0);
insert into T_EG (ID, INTERNE__ID, NAME, BESCHREIBUNG, TYP, VERFUEGBAR_VON, VERFUEGBAR_BIS, CONFIGURABLE)
	values (11, 11, 'ADSL-Modem', 'ADSL-Modem', 4, '2006-08-01', '2200-01-01', 0);
insert into T_EG (ID, INTERNE__ID, NAME, BESCHREIBUNG, TYP, VERFUEGBAR_VON, VERFUEGBAR_BIS, CONFIGURABLE)
	values (12, 12, 'SDSL-Router', 'Router fuer SDSL-Anschluesse', 4, '2006-08-01', '2200-01-01', 1);
insert into T_EG (ID, INTERNE__ID, NAME, BESCHREIBUNG, TYP, VERFUEGBAR_VON, VERFUEGBAR_BIS, CONFIGURABLE)
	values (13, 13, 'ADSL-Router', 'ADSL-Modem mit Router-Funktion', 4, '2006-08-01', '2200-01-01', 0);
insert into T_EG (ID, INTERNE__ID, NAME, BESCHREIBUNG, TYP, VERFUEGBAR_VON, VERFUEGBAR_BIS, CONFIGURABLE)
	values (14, 14, 'unbekannt', '', 4, '2006-08-01', '2200-01-01', 0);
			
-- Endgeraete-Konfiguration uebernehmen
delete from t_eg_config;
delete from T_EG_2_AUFTRAG where AUFTRAG_ID is not null;

-- fuer Eintraege aus t_ip_endgeraet Datensaetze in t_eg_2_auftrag erstellen
insert into T_EG_2_AUFTRAG (AUFTRAG_ID, EG_ID, EG_HERKUNFT)
    select ip.AUFTRAG_ID, 12, 273 from t_ip_endgeraet ip where gueltig_bis='2200-01-01';

-- Eintraege aus t_ip_endgeraet nach t_eg_config uebernehmen
insert into T_EG_CONFIG (IP_ENDGERAET__ID, HERSTELLER, MODELL, SERIAL_NUMBER, TYP, NAT, IP_LAN, SUBNETMASK,
	DHCP, ANZAHL_IP, EG_COUNT_ACT, EG_COUNT_1YEAR, EG_COUNT_2YEARS, IP_VIRTUELL_WEB, BEMERKUNG, ANSP_ADMIN,
	ANSP_TECHNICAL, BEARBEITER, EG_USER, EG_PASSWORD, GUELTIG_VON, GUELTIG_BIS)
	select IP_ENDGERAET__ID, HERSTELLER, MODELL, SERIAL_NUMBER, TYP, NAT, IP_LAN, SUBNETMASK,
     	DHCP, ANZAHL_IP, EG_COUNT_ACT, EG_COUNT_1YEAR, EG_COUNT_2YEARS, IP_VIRTUELL_WEB, BEMERKUNG, ANSP_ADMIN,
	    ANSP_TECHNICAL, BEARBEITER, EG_USER, EG_PASSWORD, GUELTIG_VON, GUELTIG_BIS
	  from t_ip_endgeraet;
create temporary table tmp_egcnf 
  select ip.IP_ENDGERAET__ID, eg2a.ID as EG2A_ID, eg2a.AUFTRAG_ID
	from t_ip_endgeraet ip, t_eg_2_auftrag eg2a
		where ip.gueltig_bis='2200-01-01' and ip.auftrag_id=eg2a.AUFTRAG_ID;
update T_EG_CONFIG cnf inner join tmp_egcnf tmp on tmp.IP_ENDGERAET__ID=cnf.IP_ENDGERAET__ID
   set cnf.EG2A_ID=tmp.EG2A_ID;
drop table if exists tmp_egcnf;

-- bestehende Daten von T_ENDGERAET auf T_EG_2_AUFTRAG schreiben
-- NTs
insert into T_EG_2_AUFTRAG (AUFTRAG_ID, EG_ID, EG_HERKUNFT, MONTAGEART)
  select atech.AUFTRAG_ID, 10, eg.EG_HERKUNFT_ID, es.NT_MONTAGEART 
    from t_endgeraet eg 
      inner join t_endstelle es on eg.ES_ID=es.ID
      inner join t_auftrag_technik atech on es.ES_GRUPPE=atech.AT_2_ES_ID
      where atech.GUELTIG_BIS>='2200-01-01'
      and eg.EG_TYP_ID=271 and eg.GUELTIG_BIS>='2200-01-01';
-- Modems (=ADSL-Modems)
insert into T_EG_2_AUFTRAG (AUFTRAG_ID, EG_ID, EG_HERKUNFT, MONTAGEART)
  select atech.AUFTRAG_ID, 11, eg.EG_HERKUNFT_ID, es.NT_MONTAGEART 
    from t_endgeraet eg 
      inner join t_endstelle es on eg.ES_ID=es.ID
      inner join t_auftrag_technik atech on es.ES_GRUPPE=atech.AT_2_ES_ID
      where atech.GUELTIG_BIS>='2200-01-01'
      and eg.EG_TYP_ID=272 and eg.GUELTIG_BIS>='2200-01-01';
-- SDSL-Produkte --> SDSL-Router als Endgeraet (statt Modem)
update T_EG_2_AUFTRAG e2a 
  inner join T_AUFTRAG_DATEN a on e2a.AUFTRAG_ID=a.AUFTRAG_ID
  inner join T_PRODUKT p on a.PROD_ID=p.PROD_ID
  inner join T_PRODUKTGRUPPE pg on p.PRODUKTGRUPPE_ID=pg.ID
    set EG_ID=12 where a.GUELTIG_BIS>='2200-01-01' and 
    pg.ID=4 and EG_ID is not null;
-- DSL-Produkte --> ADSL-Modem als Endgeraet, wo 'null'
update T_EG_2_AUFTRAG e2a 
  inner join T_AUFTRAG_DATEN a on e2a.AUFTRAG_ID=a.AUFTRAG_ID
  inner join T_PRODUKT p on a.PROD_ID=p.PROD_ID
    set EG_ID=11 where a.GUELTIG_BIS>='2200-01-01' and 
    p.PROD_ID in (9,56,57,315,316,317,320,321,324,325,326,327,330,331,332,333,339) and EG_ID is null;
-- ISDN-Produkte --> NT als Endgeraet, wo 'null'
update T_EG_2_AUFTRAG e2a 
  inner join T_AUFTRAG_DATEN a on e2a.AUFTRAG_ID=a.AUFTRAG_ID
  inner join T_PRODUKT p on a.PROD_ID=p.PROD_ID
    set EG_ID=10 where a.GUELTIG_BIS>='2200-01-01' and 
    p.PROD_ID in (1,2,10,12,37,38,319,323,329,335,336,337) and (EG_ID is null or EG_ID<>10);
-- Uebernahmen (ohne EG-Typ) --> 'unbekannt'
insert into T_EG_2_AUFTRAG (AUFTRAG_ID, EG_ID, EG_HERKUNFT, MONTAGEART)
  select atech.AUFTRAG_ID, 14, eg.EG_HERKUNFT_ID, es.NT_MONTAGEART 
    from t_endgeraet eg 
      inner join t_endstelle es on eg.ES_ID=es.ID
      inner join t_auftrag_technik atech on es.ES_GRUPPE=atech.AT_2_ES_ID
      where atech.GUELTIG_BIS>='2200-01-01'
      and eg.EG_TYP_ID is null;


-- Endgeraet-2-Produkt Konfiguration
delete from t_prod_2_eg;
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (1, 10 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (2, 10 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (9, 11 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (9, 13, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (10, 10 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (37, 10 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (38, 10 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (56, 11 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (56, 13, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (57, 11 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (57, 13, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (315, 11 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (315, 13, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (316, 11 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (316, 13, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (317, 11 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (317, 13, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (320, 11 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (320, 13, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (319, 10 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (321, 11 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (321, 13, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (323, 10 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (324, 11 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (324, 13, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (325, 11 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (325, 13, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (326, 11 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (326, 13, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (327, 11 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (327, 13, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (330, 11 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (330, 13, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (331, 11 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (331, 13, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (332, 11 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (332, 13, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (333, 11 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (333, 13, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (335, 10 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (336, 10 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (337, 10 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (339, 11 ,1);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (339, 13, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (60, 12, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (61, 12, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (62, 12, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (63, 12, 0);
insert into T_PROD_2_EG (PROD_ID, EG_ID, IS_DEFAULT) values (64, 12, 0);


