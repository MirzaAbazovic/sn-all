-- technische Leistungen f�r TV-Signallieferung

--  Taifun:  79719 �/MV-TV Signalliefer > 1 WE Quartals NS
INSERT INTO T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE,
 STR_VALUE, PROD_NAME_STR, DESCRIPTION, DISPO, EWSD,
 SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON,
 GUELTIG_BIS, VERSION, CHANGED_AT)
VALUES
  (151, 'TV Signallieferung MV > 1 WE Quartals NS', 79719, 'TV', '',
   '', ' ', '', '0', '0',
   '0', '0', '0', '1', TO_DATE('01/11/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, sysdate);


--   Taifun:  79720 �/MV-TV Signalliefer > 1 WE Quartals VS
INSERT INTO T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE,
 STR_VALUE, PROD_NAME_STR, DESCRIPTION, DISPO, EWSD,
 SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON,
 GUELTIG_BIS, VERSION, CHANGED_AT)
VALUES
  (152, 'TV Signallieferung MV > 1 WE Quartals VS', 79720, 'TV', '',
   '', ' ', '', '0', '0',
   '0', '0', '0', '1', TO_DATE('01/11/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, sysdate);


--   Taifun:   79721 �/MV-TV Signalliefer > 1 WE j�hrlich NS
INSERT INTO T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE,
 STR_VALUE, PROD_NAME_STR, DESCRIPTION, DISPO, EWSD,
 SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON,
 GUELTIG_BIS, VERSION, CHANGED_AT)
VALUES
  (153, 'TV Signallieferung MV > 1 WE j�hrlich NS', 79721, 'TV', '',
   '', ' ', '', '0', '0',
   '0', '0', '0', '1', TO_DATE('01/11/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, sysdate);

--    Taifun:   79722 �/MV-TV Signalliefer > 1 WE j�hrlich VS
INSERT INTO T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE,
 STR_VALUE, PROD_NAME_STR, DESCRIPTION, DISPO, EWSD,
 SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON,
 GUELTIG_BIS, VERSION, CHANGED_AT)
VALUES
  (154, 'TV Signallieferung MV > 1 WE j�hrlich VS', 79722, 'TV', '',
   '', ' ', '', '0', '0',
   '0', '0', '0', '1', TO_DATE('01/11/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, sysdate);


--   Taifun:  79723 �/MV-TV Signalliefer > 1 WE monatlich NS
INSERT INTO T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE,
 STR_VALUE, PROD_NAME_STR, DESCRIPTION, DISPO, EWSD,
 SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON,
 GUELTIG_BIS, VERSION, CHANGED_AT)
VALUES
  (155, 'TV Signallieferung MV > 1 WE monatlich NS', 79723, 'TV', '',
   '', ' ', '', '0', '0',
   '0', '0', '0', '1', TO_DATE('01/11/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, sysdate);

--   Taifun:  79724 �/MV-TV Signalliefer > 1 WE monatlich VS
 INSERT INTO T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE,
 STR_VALUE, PROD_NAME_STR, DESCRIPTION, DISPO, EWSD,
 SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON,
 GUELTIG_BIS, VERSION, CHANGED_AT)
VALUES
  (156, 'TV Signallieferung MV > 1 WE monatlich VS', 79724, 'TV', '',
   '', ' ', '', '0', '0',
   '0', '0', '0', '1', TO_DATE('01/11/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, sysdate);

--Taifun Leistungen f�r Produkt TV-Signallieferung-MV

INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 522, 151, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 522, 152, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 522, 153, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 522, 154, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 522, 155, null, 0);
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT) VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 522, 156, null, 0);








