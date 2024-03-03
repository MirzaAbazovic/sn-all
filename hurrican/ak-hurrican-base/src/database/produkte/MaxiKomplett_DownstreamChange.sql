--
-- SQL-Script fuer die Aenderung der MaxiKomplett Bandbreiten:
-- 1000 --> 1500
-- 2000 --> 3000
--


-- muss vor dem Script 'MaxiKomplettAktion200703Script' ausgefuehrt werden!!!
--alter session set nls_date_format='yyyy-mm-dd'; 
--update auftragpos set parameter='1000' where parameter='1500' and charge_from<'2007-03-01';
--commit;


-- bereits in produktiver DB ausgefuehrt!
--insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE, STR_VALUE,
--	DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS) 
--	values (18, '1500 kbit/s', 10020, 'DOWNSTREAM', 1500, '1500',
--	false, false, true, false, false, true, '2007-02-01', '2200-01-01');

-- Bandbreite 1500 den Produkten 420 und 421 als moegliche techn. Leistung zuordnen
--insert into t_prod_2_tech_leistung (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (420, 18, 0);
--insert into t_prod_2_tech_leistung (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (421, 18, 0);
--insert into t_prod_2_tech_leistung (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) values (420, 13, 18, 1);
--insert into t_prod_2_tech_leistung (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) values (421, 13, 18, 1);
	
-- 1000 kbit/s = 8
-- 2000 kbit/s = 9
-- 3000 kbit/s = 10

-- Auftrag2TechLs aendern:
-- Script ChangeMaxiKomplettDownstreamScript.java ausfuehren!!!


-- Aenderungen an Taifun-Leistungen:
-- Ü/Maxi DSL 1500 - techn. Konfiguration aendern
update LEISTUNG set EXT_LEISTUNG__NO=10020 where LEISTUNG_NO=36792;
commit;

-- div. Leistungen zum 28.02.2007 beenden
alter session set nls_date_format='yyyy-mm-dd';

-- pruefen, welche Leistungen die beendet werden sollen, noch im Maerz gebucht sind
select ap.order__no, ap.charge_from, ap.charge_to, ap.charged_until 
  from auftragpos ap, auftrag a where ap.order__no=a.auftrag__no and a.HIST_STATUS<>'UNG' 
  and service_elem__no=20408 and (charge_to is null or charge_to>='2007-03-01');
select ap.order__no, ap.charge_from, ap.charge_to, ap.charged_until 
  from auftragpos ap, auftrag a where ap.order__no=a.auftrag__no and a.HIST_STATUS<>'UNG' 
  and service_elem__no=23302 and (charge_to is null or charge_to>='2007-03-01');
select ap.order__no, ap.charge_from, ap.charge_to, ap.charged_until 
  from auftragpos ap, auftrag a where ap.order__no=a.auftrag__no and a.HIST_STATUS<>'UNG' 
  and service_elem__no=22479 and (charge_to is null or charge_to>='2007-03-01');
select ap.order__no, ap.charge_from, ap.charge_to, ap.charged_until 
  from auftragpos ap, auftrag a where ap.order__no=a.auftrag__no and a.HIST_STATUS<>'UNG' 
  and service_elem__no=23390 and (charge_to is null or charge_to>='2007-03-01');

-- 
-- nur ausfuehren, wenn obige Selects keine Results besitzen - sonst Positionen manuell korrigieren!
--
-- "Ü/Maxi DSL 2000" beenden
update LEISTUNG set GUELTIG_BIS='2007-02-28' where leistung_no=31395;
-- "1663005: Maxi Kom 30" beenden (MUC: Herbst-Aktion 3000)
update LEISTUNG set GUELTIG_BIS='2007-02-28' where leistung_no=33747;
-- "1666005: Maxi Kom 60" beenden (MUC: Herbst-Aktion 6000)
update LEISTUNG set GUELTIG_BIS='2007-02-28' where leistung_no=32980;
-- "1662005: Maxi Kom 20" beenden (MUC: Herbst-Aktion 2000)
update LEISTUNG set GUELTIG_BIS='2007-02-28' where leistung_no=33830;

commit;

--
-- Update in Hurrican
--

-- Verlaufs-ID (Kuendigung) fuer beendete Leistungen setzen
update t_auftrag_2_tech_ls set verlauf_id_kuend=verlauf_id_real 
  where aktiv_bis='2007-02-28' and tech_ls_id in (8,9)
  and verlauf_id_kuend is null;


