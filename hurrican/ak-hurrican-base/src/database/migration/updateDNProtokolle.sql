--
-- SQL-Script um auch 'analog' als Phone-Protokoll aufzunehmen.
-- Ausserdem werden 1TR6 und DSS1 als Protokoll in allen Produkten/Auftraegen >= Maxi/Premium
-- mit aufgenommen.
-- 'TK' als ISDN-Typ in Premium-Produkten wird eingefuehrt.
--

ALTER TABLE t_prod_2_tech_leistung
  ADD CONSTRAINT FK_P2TL_2_TL
      FOREIGN KEY (tech_ls_id)
      REFERENCES T_TECH_LEISTUNG (ID)
    ON DELETE NO ACTION
    ON UPDATE CASCADE;

-- Inhalt der 'alten' Mapping-Tabelle entfernen
delete from t_prod_2_tech_ls;

-- Phone-Protokoll 'analog' und Default-Zuordnung zu entsprechenden Produkten (nur neue Produkte)
insert into t_tech_leistung (ID, NAME, EXTERN_LEISTUNG__NO, TYP, STR_VALUE, DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS) 
	values (102, 'analog', 20002, 'PHONE_PROTOKOLL', 'analog', 0, 1, 0, 0, 0, 1, '2006-10-01', '2200-01-01'); 
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (421, 102, null, 1);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (431, 102, null, 1);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (340, 102, null, 1);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (322, 102, null, 1);

-- Phone-Protokoll 'DSS1' zu entsprechenden Produkten zuordnen (nur neue Produkte)
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (420, 100, null, 1);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (430, 100, null, 1);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (323, 100, null, 1);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (336, 100, null, 1);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (337, 100, null, 1);
-- Phone-Protokoll '1TR6' zu entsprechenden Produkten zuordnen (nur neue Produkte)
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (337, 101, null, 0);

-- fuer ISDN TK eine zusaetzliche Leistung einfuehren
insert into t_tech_leistung (ID, NAME, EXTERN_LEISTUNG__NO, TYP, STR_VALUE, DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS) 
	values (103, 'TK', 20003, 'ISDN_TYP', 'TK', 0, 1, 0, 0, 0, 1, '2006-10-01', '2200-01-01'); 
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (337, 103, null, 1);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (430, 103, null, 0);

-- DSS1 nachtragen
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON) 
	select auftrag_id, 100, 1, '1999-01-01' from t_auftrag_daten where prod_id in (420,430,323,336,337,335) 
	  and gueltig_bis='2200-01-01' and status_id<9000 and status_id not in (1150,3400)
	  and prodak_order__no not in (373696,377202,377206,377828,387698,396623);
-- 1TR6 nachtragen
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON) 
	select auftrag_id, 101, 1, '1999-01-01' from t_auftrag_daten where prod_id in (420,430,323,336,337,335) 
	  and gueltig_bis='2200-01-01' and status_id<9000 and status_id not in (1150,3400)
	  and prodak_order__no in (373696,377202,377206,377828,387698,396623);
-- analog nachtragen
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON) 
	select auftrag_id, 102, 1, '1999-01-01' from t_auftrag_daten where prod_id in (421,431,340,322) 
	  and gueltig_bis='2200-01-01' and status_id<9000 and status_id not in (1150,3400);
-- TK nachtragen
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON) 
	select auftrag_id, 103, 1, '1999-01-01' from t_auftrag_daten where prod_id in (337) 
	  and gueltig_bis='2200-01-01' and status_id<9000 and status_id not in (1150,3400);

-- Nachtraege
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON) 
	select auftrag_id, 102, 1, '1999-01-01' from t_auftrag_daten where prod_id in (328) 
	  and gueltig_bis='2200-01-01' and status_id<9000 and status_id not in (1150,3400);
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON) 
	select auftrag_id, 102, 1, '1999-01-01' from t_auftrag_daten where prod_id in (334) 
	  and gueltig_bis='2200-01-01' and status_id<9000 and status_id not in (1150,3400);
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON) 
	select auftrag_id, 100, 1, '1999-01-01' from t_auftrag_daten where prod_id in (329) 
	  and gueltig_bis='2200-01-01' and status_id<9000 and status_id not in (1150,3400)
	  and prodak_order__no not in (373696,377202,377206,377828,387698,396623);



-- TK-Auftraege, die bereits in einem Kombi-Produkt sind (ADSL+TK)
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON)
   values (167708, 103, 1, '1999-01-01');
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON)
   values (167676, 103, 1, '1999-01-01');
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON)
   values (167986, 103, 1, '1999-01-01');
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON)
   values (167578, 103, 1, '1999-01-01');
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON)
   values (167866, 103, 1, '1999-01-01');
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON)
   values (167694, 103, 1, '1999-01-01');
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON)
   values (167856, 103, 1, '1999-01-01');
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON)
   values (167658, 103, 1, '1999-01-01');
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON)
   values (167689, 103, 1, '1999-01-01');
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON)
   values (167702, 103, 1, '1999-01-01');
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON)
   values (167901, 103, 1, '1999-01-01');
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON)
   values (167692, 103, 1, '1999-01-01');
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON)
   values (167970, 103, 1, '1999-01-01');
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON)
   values (167645, 103, 1, '1999-01-01');
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON)
   values (167905, 103, 1, '1999-01-01');
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON)
   values (168028, 103, 1, '1999-01-01');
insert into t_auftrag_2_tech_ls (AUFTRAG_ID, TECH_LS_ID, QUANTITY, AKTIV_VON)
   values (167627, 103, 1, '1999-01-01');


-- aktiv-von Datum und Verlaufs-ID Realisierung eintragen
update t_auftrag_2_tech_ls atl
	inner join t_verlauf v on atl.auftrag_id=v.auftrag_id
	  and v.verlauf_status_id not in (4910) and v.verlauf_status_id<9000
    set atl.verlauf_id_real=v.id where atl.aktiv_von='1999-01-01' and atl.verlauf_id_real is null;
update t_auftrag_2_tech_ls atl 
	inner join t_auftrag_daten ad on atl.auftrag_id=ad.auftrag_id
	  and ad.gueltig_bis='2200-01-01'
	set atl.aktiv_von=ad.vorgabe_scv where atl.aktiv_von='1999-01-01';
update t_auftrag_2_tech_ls set aktiv_von='2006-10-01' where aktiv_von is null or aktiv_von='0000-00-00' and tech_ls_id>=100;

-- aktiv-bis Datum und Verlaufs-ID Kuendigung eintragen
update t_auftrag_2_tech_ls atl 
	inner join t_auftrag_daten ad on atl.auftrag_id=ad.auftrag_id
	  and ad.gueltig_bis='2200-01-01'
	set atl.aktiv_bis=ad.kuendigung where atl.aktiv_bis is null and ad.kuendigung is not null;
update t_auftrag_2_tech_ls atl
	inner join t_verlauf v on atl.auftrag_id=v.auftrag_id
	  and v.verlauf_status_id not in (9195) and v.verlauf_status_id>=9000
    set atl.verlauf_id_kuend=v.id where atl.aktiv_bis is not null and atl.verlauf_id_kuend is null;


-- Endstellenbemerkung erweitern
alter table t_endstelle change bemstawa BEMSTAWA VARCHAR(255);

--
-- ORACLE-Updates!!!
--
-- TK-Leistung nachtragen
update leistung set EXT_LEISTUNG__NO=20003 
    where LEISTUNG__NO in (20230,20232,17037,17091,17038,17092,17381,17383,17382,17384) and HIST_STATUS='AKT';
-- 1TR6 als Leistung in OE__NO 43 und 45 definieren
update leistung set EXT_LEISTUNG__NO=20001 
    where LEISTUNG__NO in (17091,17092,17383,17384,18266,17200,18270) and HIST_STATUS='AKT';





