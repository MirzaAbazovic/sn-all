-- Technische Leistung für Bandbreite 15Mbit anlegen

insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_MISC__NO, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE, STR_VALUE, PARAMETER, PROD_NAME_STR, DESCRIPTION, DISPO, EWSD, SDH, IPS, SCT, CHECK_QUANTITY, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS)
values (268, '15000 kbit/s', null, 10039, 'DOWNSTREAM', 15000, '15000', null, '15000', null, 0, 0, 0, 1, 0, null, 1, to_date('01.11.2009', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'));

insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (s_t_prod_2_tech_leistung_0.nextval, 460, 268, null, 0);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
values (s_t_prod_2_tech_leistung_0.nextval, 460, 16, null, 0);
