-- techn. Leistung 2000 kbit/s Downstream
insert into T_TECH_LEISTUNG
  (ID, NAME, EXTERN_LEISTUNG__NO, TYP,
   LONG_VALUE, STR_VALUE, PROD_NAME_STR, DESCRIPTION,
   DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL,
   GUELTIG_VON,
   GUELTIG_BIS)
 values
  (69, '2000 kbit/s', 10069, 'DOWNSTREAM',
   2000, '2000', '2000', 'Downstream 2.000 kbit/s',
   '0', '0', '0', '1', '0', '1',
   TO_DATE('08/01/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'));

-- techn. Leistung 4000 kbit/s Upstream
insert into T_TECH_LEISTUNG
  (ID, NAME, EXTERN_LEISTUNG__NO, TYP,
   LONG_VALUE, STR_VALUE, PROD_NAME_STR, DESCRIPTION,
   DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL,
   GUELTIG_VON,
   GUELTIG_BIS)
 values
   (70, '4000 kbit/s', 10070, 'UPSTREAM',
    4000, '4000', '4000', 'Upstream 4.000 kbit/s',
    '0', '0', '0', '1', '0','1',
    TO_DATE('08/01/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
    TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'));


-- techn. Leistungen fuer SDSL-T:
Insert into T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR,
 DISPO, EWSD, SDH, IPS, SCT,
 SNAPSHOT_REL, GUELTIG_VON,
 GUELTIG_BIS, VERSION, CHANGED_AT)
Values
  (590, 'SDSL-T', 10240, 'REALVARIANTE', 'T',
   '0', '0', '0', '0', '0',
   '1', TO_DATE('08/01/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, TO_DATE('09/11/2013 11:41:47', 'MM/DD/YYYY HH24:MI:SS'));

Insert into T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR,
 DISPO, EWSD, SDH, IPS, SCT,
 SNAPSHOT_REL, GUELTIG_VON,
 GUELTIG_BIS, VERSION, CHANGED_AT)
Values
  (591, 'SDSL-T 2000', 10241, 'KOMBI', '.',
   '0', '0', '0', '0', '0',
   '1', TO_DATE('08/01/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, TO_DATE('09/11/2013 11:41:47', 'MM/DD/YYYY HH24:MI:SS'));

Insert into T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR,
 DISPO, EWSD, SDH, IPS, SCT,
 SNAPSHOT_REL, GUELTIG_VON,
 GUELTIG_BIS, VERSION, CHANGED_AT)
Values
  (592, 'SDSL-T 4000', 10242, 'KOMBI', '.',
   '0', '0', '0', '0', '0',
   '1', TO_DATE('08/01/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, TO_DATE('09/11/2013 11:41:47', 'MM/DD/YYYY HH24:MI:SS'));

Insert into T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR,
 DISPO, EWSD, SDH, IPS, SCT,
 SNAPSHOT_REL, GUELTIG_VON,
 GUELTIG_BIS, VERSION, CHANGED_AT)
Values
  (593, 'SDSL-T 6000', 10243, 'KOMBI', '.',
   '0', '0', '0', '0', '0',
   '1', TO_DATE('08/01/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, TO_DATE('09/11/2013 11:41:47', 'MM/DD/YYYY HH24:MI:SS'));

Insert into T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR,
 DISPO, EWSD, SDH, IPS, SCT,
 SNAPSHOT_REL, GUELTIG_VON,
 GUELTIG_BIS, VERSION, CHANGED_AT)
Values
  (594, 'SDSL-T 10000', 10244, 'KOMBI', '.',
   '0', '0', '0', '0', '0',
   '1', TO_DATE('08/01/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, TO_DATE('09/11/2013 11:41:47', 'MM/DD/YYYY HH24:MI:SS'));

Insert into T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR,
 DISPO, EWSD, SDH, IPS, SCT,
 SNAPSHOT_REL, GUELTIG_VON,
 GUELTIG_BIS, VERSION, CHANGED_AT)
Values
  (595, 'SDSL-T 20000', 10245, 'KOMBI', '.',
   '0', '0', '0', '0', '0',
   '1', TO_DATE('08/01/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, TO_DATE('09/11/2013 11:41:47', 'MM/DD/YYYY HH24:MI:SS'));


-- KOMBI 2000
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 591, '0', 0);

-- REALVARIANTE
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 590, 591, '1', 0);

-- DS 2000
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 69, 591, '1', 0);

-- US 2000
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 26, 591, '1', 0);


-- KOMBI 4000
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 592, '0', 0);

-- REALVARIANTE
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 590, 592, '1', 0);

-- DS 4000
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 16, 592, '1', 0);

-- US 4000
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 70, 592, '1', 0);


-- KOMBI 6000
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 593, '0', 0);

-- REALVARIANTE
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 590, 593, '1', 0);

-- DS 6000
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 11, 593, '1', 0);

-- US 6000
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 486, 593, '1', 0);


-- KOMBI 10000
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 594, '0', 0);

-- REALVARIANTE
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 590, 594, '1', 0);

-- DS 10000
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 33, 594, '1', 0);

-- US 10000
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 28, 594, '1', 0);


-- KOMBI 20000
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 595, '0', 0);

-- REALVARIANTE
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 590, 595, '1', 0);

-- DS 20000
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 39, 595, '1', 0);

-- US 20000
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 54, 595, '1', 0);


-- Fastpath
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448,  1, '0', 0);
-- Always-On
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448,  3, '0', 0);
-- QoS
Insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 448, 50, '0', 0);
