--
-- DB-Aenderungen fuer das Produkt Maxi Deluxe
--

-- Rufnummern-Leistung
-- Neues Leistungsbündel
insert into T_LEISTUNGSBUENDEL (ID, NAME, BESCHREIBUNG)
values (14, 'Glasfaser-DSL VoIP', 'Leistungsbündel für Maxi Glasfaser-DSL Produkte');


insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (S_T_LB_2_LEISTUNG_0.nextval, 14, 1, 60, '1', '2010-01-21', '2200-01-01', '2010-01-21', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (S_T_LB_2_LEISTUNG_0.nextval, 14, 5, 60, '1', '2010-01-21', '2200-01-01', '2010-01-21', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (S_T_LB_2_LEISTUNG_0.nextval, 14, 7, 60, '1', '2010-01-21', '2200-01-01', '2010-01-21', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (S_T_LB_2_LEISTUNG_0.nextval, 14, 13, 60, '1', '2010-01-21', '2200-01-01', '2010-01-21', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (S_T_LB_2_LEISTUNG_0.nextval, 14, 26, 60, '1', '2010-01-21', '2200-01-01', '2010-01-21', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (S_T_LB_2_LEISTUNG_0.nextval, 14, 28, 60, '1', '2010-01-21', '2200-01-01', '2010-01-21', '2200-01-01', '25');
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (S_T_LB_2_LEISTUNG_0.nextval, 14, 29, 60, '1', '2010-01-21', '2200-01-01', '2010-01-21', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (S_T_LB_2_LEISTUNG_0.nextval, 14, 46, 60, '1', '2010-01-21', '2200-01-01', '2010-01-21', '2200-01-01', '60');
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (S_T_LB_2_LEISTUNG_0.nextval, 14, 33, 60, '0', '2010-01-21', '2200-01-01', '2010-01-21', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (S_T_LB_2_LEISTUNG_0.nextval, 14, 34, 60, '0', '2010-01-21', '2200-01-01', '2010-01-21', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (S_T_LB_2_LEISTUNG_0.nextval, 14, 2, 60, '0', '2010-01-21', '2200-01-01', '2010-01-21', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (S_T_LB_2_LEISTUNG_0.nextval, 14, 6, 60, '0', '2010-01-21', '2200-01-01', '2010-01-21', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (S_T_LB_2_LEISTUNG_0.nextval, 14, 14, 60, '0', '2010-01-21', '2200-01-01', '2010-01-21', '2200-01-01', NULL);
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (S_T_LB_2_LEISTUNG_0.nextval, 14, 58, 60, '1', '2010-01-21', '2200-01-01', '2010-01-21', '2200-01-01', '1');
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
 values (S_T_LB_2_LEISTUNG_0.nextval, 14, 59, 60, '1', '2010-01-21', '2200-01-01', '2010-01-21', '2200-01-01', NULL);

