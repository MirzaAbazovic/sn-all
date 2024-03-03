--
-- SQL-Scripte, um die Produktkonfiguration Connect von
-- MNETTEST nach MNETPROD zu kopieren.
--


CREATE DATABASE LINK TMP_DBL2MNETTEST
 CONNECT TO MNETTEST
 IDENTIFIED BY "1TSETTENM"
 USING 'MNETTEST';
commit;

-- TODO (manuell): DB-Link testen!
-- select count(*) from auftrag@TMP_DBL2MNETTEST;

insert into OE (select * from OE@TMP_DBL2MNETTEST where oe_no between 2156 and 2162);
insert into OE (select * from OE@TMP_DBL2MNETTEST where oe_no=2192);

insert into OE_LANG (select * from OE_LANG@TMP_DBL2MNETTEST where oe_no between 2156 and 2162);
insert into OE_LANG (select * from OE_LANG@TMP_DBL2MNETTEST where oe_no=2192);

insert into LEISTUNG (select * from LEISTUNG@TMP_DBL2MNETTEST where oe__no between 2156 and 2162);
insert into LEISTUNG (select * from LEISTUNG@TMP_DBL2MNETTEST where oe__no=2192);

insert into SERVICE_VALUE_PRICE (select * from SERVICE_VALUE_PRICE@TMP_DBL2MNETTEST where LEISTUNG_NO>=40748 and LEISTUNG_NO not in(40841));

insert into LEISTUNG_LANG (select * from LEISTUNG_LANG@TMP_DBL2MNETTEST where LEISTUNG_LANG_NO>=56422);
commit;

drop database link TMP_DBL2MNETTEST;
commit;


-- Sequences neu definieren
select max(OE_NO) from OE;
drop sequence S_OE_0;
create sequence S_OE_0 start with xxx;
grant select on S_OE_0 to public;
commit;

select max(LEISTUNG_NO) from LEISTUNG;
drop sequence S_LEISTUNG_0;
create sequence S_LEISTUNG_0 start with xxx;
grant select on S_LEISTUNG_0 to public;
commit;

select max(LEISTUNG_LANG_NO) from LEISTUNG_LANG;
drop sequence S_LEISTUNG_LANG_0;
create sequence S_LEISTUNG_LANG_0 start with xxx;
grant select on S_LEISTUNG_LANG_0 to public;
commit;




