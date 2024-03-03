--
-- Update-Script fuer die Maxi-Massnahmen 12/2007
-- (erhöhter Down-/Upstream)
--

--insert into t_tech_leistung (ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE, STR_VALUE,
--    DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS) 
--    values (22, '1000 kbit/s', 10024, 'UPSTREAM', 1000, '1000',
--    0, 0, 1, 0, 0, 1, '2007-11-01', '2200-01-01');

-- Downstream 18000 den Maxi-Produkten zuordnen
-- done at 2007-12-30
insert into t_prod_2_tech_leistung (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) 
	values (420, 21, null, null);
insert into t_prod_2_tech_leistung (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) 
	values (421, 21, null, null);
insert into t_prod_2_tech_leistung (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) 
	values (440, 21, null, null);

-- Upstream 1000 als Default fuer Downstream 18000
-- done at 2007-12-30
insert into t_prod_2_tech_leistung (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) 
	values (420, 22, 21, 1);
insert into t_prod_2_tech_leistung (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) 
	values (421, 22, 21, 1);
insert into t_prod_2_tech_leistung (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) 
	values (440, 22, 21, 1);

-- DSLAM-Profile 83+84 den Maxi-Produkten zuordnen
-- done at 2007-12-30
insert into t_prod_2_dslamprofile (PROD_ID, DSLAM_PROFILE_ID) values (420,83);
insert into t_prod_2_dslamprofile (PROD_ID, DSLAM_PROFILE_ID) values (420,84);
insert into t_prod_2_dslamprofile (PROD_ID, DSLAM_PROFILE_ID) values (421,83);
insert into t_prod_2_dslamprofile (PROD_ID, DSLAM_PROFILE_ID) values (421,84);
insert into t_prod_2_dslamprofile (PROD_ID, DSLAM_PROFILE_ID) values (440,84);

-- Auftrag2TechLs mit 16000er Leistung in temp. Tabelle schreiben
create table tmp_auftrag2techls as 
  select a2t.ID, a2t.AUFTRAG_ID, a2t.TECH_LS_ID, a2t.QUANTITY, a2t.AKTIV_VON,
    a2t.AKTIV_BIS, a2t.VERLAUF_ID_REAL, a2t.VERLAUF_ID_KUEND, a2t.TIMEST 
    from t_auftrag_2_tech_ls a2t
     inner join t_auftrag_daten ad on a2t.auftrag_id=ad.auftrag_id
     inner join t_produkt p on ad.prod_id=p.prod_id
     where ad.gueltig_bis='2200-01-01'
       and ad.status_id<9800
       and p.prod_id in (420,421,440) 
       and (a2t.aktiv_bis is null or a2t.aktiv_bis>='2007-12-01')
       and a2t.tech_ls_id=12;
-- aus TMP-Tabelle Eintraege mit 18000er Leistung und aktiv-von 2007-12-01 erzeugen
insert into t_auftrag_2_tech_ls (
  AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON, AKTIV_BIS, VERLAUF_ID_REAL, VERLAUF_ID_KUEND)
    select tmp.AUFTRAG_ID, 21, 1, '2007-12-01', tmp.AKTIV_BIS, tmp.VERLAUF_ID_REAL, tmp.VERLAUF_ID_KUEND
      from tmp_auftrag2techls tmp;

-- 16000er Leistungszuordnungen ueber TMP-Tabelle auf aktiv-bis 2007-11-30 setzen
update t_auftrag_2_tech_ls a2t 
  inner join tmp_auftrag2techls tmp on a2t.id=tmp.id
  set a2t.aktiv_bis='2007-11-30', a2t.verlauf_id_kuend=a2t.verlauf_id_real;



-- Auftrag2TechLs mit 800er Leistung (Upstream) in temp. Tabelle schreiben
create table tmp_auftrag2techls1000 as 
  select a2t.ID, a2t.AUFTRAG_ID, a2t.TECH_LS_ID, a2t.QUANTITY, a2t.AKTIV_VON,
    a2t.AKTIV_BIS, a2t.VERLAUF_ID_REAL, a2t.VERLAUF_ID_KUEND, a2t.TIMEST 
    from t_auftrag_2_tech_ls a2t
     inner join t_auftrag_daten ad on a2t.auftrag_id=ad.auftrag_id
     inner join t_produkt p on ad.prod_id=p.prod_id
     where ad.gueltig_bis='2200-01-01'
       and ad.status_id<9800
       and p.prod_id in (420,421,440) 
       and (a2t.aktiv_bis is null or a2t.aktiv_bis>='2007-12-01')
       and a2t.tech_ls_id=15;
-- aus TMP-Tabelle Eintraege mit 1000er Leistung und aktiv-von 2007-12-01 erzeugen
insert into t_auftrag_2_tech_ls (
  AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON, AKTIV_BIS, VERLAUF_ID_REAL, VERLAUF_ID_KUEND)
    select tmp.AUFTRAG_ID, 22, 1, '2007-12-01', tmp.AKTIV_BIS, tmp.VERLAUF_ID_REAL, tmp.VERLAUF_ID_KUEND
      from tmp_auftrag2techls1000 tmp;

-- 800er Leistungszuordnungen ueber TMP-Tabelle auf aktiv-bis 2007-11-30 setzen
update t_auftrag_2_tech_ls a2t 
  inner join tmp_auftrag2techls1000 tmp on a2t.id=tmp.id
  set a2t.aktiv_bis='2007-11-30', a2t.verlauf_id_kuend=a2t.verlauf_id_real;
  
  
  
--
-- Bauauftraege mit DSLAM-Profil 16000 ermitteln
--  
select v.auftrag_id from t_verlauf v
  inner join t_auftrag_2_dslamprofile d on v.auftrag_id=d.auftrag_id
  where v.anlass in (27,43,56)
  and d.dslam_profile_id in (71,73)
  and d.gueltig_bis='2200-01-01' and v.akt=1;  
  
update t_auftrag_2_dslamprofile set dslam_profile_id=83 where dslam_profile_id=71
  and gueltig_bis='2200-01-01' and auftrag_id in (193518,194118,198915,199778,200346,198370,201046,200673,201011,
    200771,200132,201771,201318,202190,201907,202032,202237,201581,
    202402,202229,199559,200029,202046);
  
update t_auftrag_2_dslamprofile set dslam_profile_id=84 where dslam_profile_id=73
  and gueltig_bis='2200-01-01' and auftrag_id in (193518,194118,198915,199778,200346,198370,201046,200673,201011,
    200771,200132,201771,201318,202190,201907,202032,202237,201581,
    202402,202229,199559,200029,202046);
  



