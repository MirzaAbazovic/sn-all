--
-- Leistungsbündel für SIP-Trunk
--
insert into T_LEISTUNGSBUENDEL (ID, NAME, BESCHREIBUNG)
  values (28, 'SIP-Trunk', 'Leistungsbündel für SIP-Trunk');

--
-- Leistungen zum Bündel hinzufügen
--

-- ACR 0
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
  values (S_T_LB_2_LEISTUNG_0.nextval, 28, 56, 60, '0', to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), null);

-- CLIP 0
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
  values (S_T_LB_2_LEISTUNG_0.nextval, 28,  5, 60, '0', to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), null);

-- CLIPNOSCR 0
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
  values (S_T_LB_2_LEISTUNG_0.nextval, 28,  4, 60, '0', to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), null);

-- CLIR1 0
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
  values (S_T_LB_2_LEISTUNG_0.nextval, 28,  6, 60, '0', to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), null);

-- CLIR2 1
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
  values (S_T_LB_2_LEISTUNG_0.nextval, 28,  7, 60, '1', to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), null);

-- DIVI 0
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
  values (S_T_LB_2_LEISTUNG_0.nextval, 28, 51, 60, '0', to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), null);

-- CFB 1
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
  values (S_T_LB_2_LEISTUNG_0.nextval, 28, 26, 60, '1', to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), null);

-- CFNR 1
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
  values (S_T_LB_2_LEISTUNG_0.nextval, 28, 28, 60, '1', to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), null);

-- PR 1
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
  values (S_T_LB_2_LEISTUNG_0.nextval, 28, 22, 60, '1', to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), null);

-- CFALD 0
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
  values (S_T_LB_2_LEISTUNG_0.nextval, 28, 27, 60, '0', to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), null);

-- MCID 0
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
  values (S_T_LB_2_LEISTUNG_0.nextval, 28, 14, 60, '0', to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), null);

-- CS_IN_WHITE 0
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
  values (S_T_LB_2_LEISTUNG_0.nextval, 28, 33, 60, '0', to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), null);

-- CS_IN_BLACK 0
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
  values (S_T_LB_2_LEISTUNG_0.nextval, 28, 34, 60, '0', to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), null);

-- OCB 1 60
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
  values (S_T_LB_2_LEISTUNG_0.nextval, 28, 46, 60, '1', to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), '60');

-- THIRDPTY 1
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
  values (S_T_LB_2_LEISTUNG_0.nextval, 28, 13, 60, '1', to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), null);

-- HOLD 1
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
  values (S_T_LB_2_LEISTUNG_0.nextval, 28, 59, 60, '1', to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), null);

-- CW 1
insert into T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
  values (S_T_LB_2_LEISTUNG_0.nextval, 28,  1, 60, '1', to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), to_date('15.11.2013', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), null);
