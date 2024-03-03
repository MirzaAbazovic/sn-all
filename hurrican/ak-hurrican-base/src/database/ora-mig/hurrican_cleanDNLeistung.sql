--
-- SQL-Script, um die Rufnummernleistungen aufzuraeumen.
--

-- Duplikate ermitteln
select LFDNR, DN_NO, LEISTUNGSBUENDEL_ID, LEISTUNG4DN_ID, SCV_REALISIERUNG, PARAMETER_ID from t_leistung_dn
group by DN_NO, LEISTUNG4DN_ID, LEISTUNGSBUENDEL_ID, SCV_REALISIERUNG, PARAMETER_ID having count(*) > 1
order by dn_no;
-- oder (ohne LFDNR)
select DN_NO, LEISTUNGSBUENDEL_ID, LEISTUNG4DN_ID, SCV_REALISIERUNG, PARAMETER_ID from t_leistung_dn
group by DN_NO, LEISTUNG4DN_ID, LEISTUNGSBUENDEL_ID, SCV_REALISIERUNG, PARAMETER_ID having count(*) > 1
order by dn_no;

-- DN-Leistungen loeschen, die am gleichen Tag noch gekuendigt wurden - also nie wirklich aktiv waren
delete from t_leistung_dn where scv_realisierung=scv_kuendigung;

-- doppelte DN-Leistungen in temp. Tabelle schreiben
create table TMP_LEISTUNG as select * from t_leistung_dn
	group by DN_NO, LEISTUNG4DN_ID, LEISTUNGSBUENDEL_ID, SCV_REALISIERUNG, PARAMETER_ID having count(*) > 1
	order by dn_no;

-- von doppelten DN-Leistungen eine loeschen
delete from t_leistung_dn USING t_leistung_dn, tmp_leistung where t_leistung_dn.lfdnr=tmp_leistung.lfdnr;

drop table TMP_LEISTUNG;


-- mehrmals pruefen, ob wirklich alle doppelten DN-Leistungen entfernt sind; sonst Vorgang nochmal ausfuehren
