-- Produkt-Mapping Connect-Basic

update t_produkt_mapping set mapping_part_type='connect' where mapping_group=462;

delete T_PROD_2_TECH_LEISTUNG where PROD_ID=462;

insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR, DISPO, EWSD, SDH, IPS, SCT, CHECK_QUANTITY, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS)
values (269, '10/100BASE-T, 2Mbit/s, Comfort', 30168, 'CONNECT_LEITUNG', ' ', 0, 0, 0, 1, 0, 1, 1, to_date('01.11.2009', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'));
insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR, DISPO, EWSD, SDH, IPS, SCT, CHECK_QUANTITY, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS)
values (270, '10/100BASE-T, 4Mbit/s, Comfort', 30169, 'CONNECT_LEITUNG', ' ', 0, 0, 0, 1, 0, 1, 1, to_date('01.11.2009', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'));
insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR, DISPO, EWSD, SDH, IPS, SCT, CHECK_QUANTITY, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS)
values (271, '10/100BASE-T, 6Mbit/s, Comfort', 30170, 'CONNECT_LEITUNG', ' ', 0, 0, 0, 1, 0, 1, 1, to_date('01.11.2009', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'));
insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR, DISPO, EWSD, SDH, IPS, SCT, CHECK_QUANTITY, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS)
values (272, '10/100BASE-T, 8Mbit/s, Comfort', 30171, 'CONNECT_LEITUNG', ' ', 0, 0, 0, 1, 0, 1, 1, to_date('01.11.2009', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'));
insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR, DISPO, EWSD, SDH, IPS, SCT, CHECK_QUANTITY, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS)
values (273, '10/100BASE-T, 10Mbit/s, Comfort', 30172, 'CONNECT_LEITUNG', ' ', 0, 0, 0, 1, 0, 1, 1, to_date('01.11.2009', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'));


INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 202, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 203, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 204, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 205, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 206, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 269, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 270, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 271, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 272, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 273, '0');
