insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR, DISPO, EWSD, SDH, IPS, SCT, CHECK_QUANTITY, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS)
values (274, '10/100BASE-T, 20Mbit/s', 30173, 'CONNECT_LEITUNG', ' ', 0, 0, 0, 1, 0, 1, 1, to_date('01.04.2010', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'));


INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 451, 274, '0');