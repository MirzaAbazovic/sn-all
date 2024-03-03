--
-- SQL-Statements, die nach dem Daten-Insert durchgefuehrt werden sollen.
--

-- Tabelle 'T_EG' aendern: Spalte typ in TYPE umbenennen
-- (mit Spaltennamen 'typ' gab es SQL-Fehler...)
alter table T_EG add TYPE NUMBER(2);
update T_EG set TYPE="T_EG"."typ";
alter table T_EG drop column "typ";
commit

alter table T_LB_2_LEISTUNG add PARAM_VALUE VARCHAR2(80);
update T_LB_2_LEISTUNG set PARAM_VALUE="def_param_value";
alter table T_LB_2_LEISTUNG drop column "def_param_value";
commit;

-- Sicherstellen, dass Feld RANG_SCHNITTSTELLE als VARCHAR verwendet wird (keine nachfolgenden Leerzeichen)
update t_equipment set RANG_SCHNITTSTELLE='H' where RANG_SCHNITTSTELLE like 'H%';
update t_equipment set RANG_SCHNITTSTELLE='N' where RANG_SCHNITTSTELLE like 'N%';
update t_equipment set RANG_SCHNITTSTELLE='FttB' where RANG_SCHNITTSTELLE like 'FttB%';
update t_equipment set RANG_SCHNITTSTELLE='LWL' where RANG_SCHNITTSTELLE like 'LWL%';
update t_equipment set RANG_SCHNITTSTELLE='PG3' where RANG_SCHNITTSTELLE like 'PG3%';
update t_equipment set RANG_SCHNITTSTELLE='CFV' where RANG_SCHNITTSTELLE like 'CFV%';
commit;

update T_ADDRESS set LAND_ID='DE' where LAND_ID='D ' or LAND_ID='D';
update T_ADDRESS set LAND_ID='AT' where LAND_ID='A ' or LAND_ID='A';
commit;

update T_AUFTRAG_IMPORT_FILES set FILE_TYPE=2 where FILE_TYPE=0;

commit;


update t_DB_QUERIES set SQL_QUERY = 'select ad.AUFTRAG_ID as AuftragId, a.KUNDE__NO as Kunde__NO, v.REALISIERUNGSTERMIN as Realisierungstermin, bv.NAME as Anlass, p.ANSCHLUSSART as Produkt, ad.BUENDEL_NR as Buendel_Nr, ad.BUENDEL_NR_HERKUNFT as Buendel_Herk, ad.INBETRIEBNAHME as Inbetriebnahme, ad.KUENDIGUNG as Kuendigung from T_VERLAUF v inner join T_AUFTRAG a on v.auftrag_id=a.id inner join T_AUFTRAG_DATEN ad on a.id=ad.auftrag_id inner join T_PRODUKT p on ad.prod_id=p.prod_id inner join T_BA_VERL_ANLASS bv on v.anlass=bv.id where v.REALISIERUNGSTERMIN between ? and ? and v.VERLAUF_STATUS_ID NOT IN (4910,9195) and ad.BUENDEL_NR is not null group by ad.BUENDEL_NR, ad.BUENDEL_NR_HERKUNFT, v.REALISIERUNGSTERMIN, ad.AUFTRAG_ID, a.KUNDE__NO, bv.NAME, p.ANSCHLUSSART, ad.INBETRIEBNAHME, ad.KUENDIGUNG order by v.REALISIERUNGSTERMIN, bv.NAME, p.ANSCHLUSSART' where id =4;

commit;


