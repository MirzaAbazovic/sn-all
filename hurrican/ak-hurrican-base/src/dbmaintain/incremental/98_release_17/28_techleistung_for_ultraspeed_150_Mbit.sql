---- Down-/Upstream Leistungen --------------------------------------------------
-- techn. Leistung 150 Mbit/s Downstream
INSERT INTO T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP,
 LONG_VALUE, STR_VALUE, PROD_NAME_STR, DESCRIPTION,
 DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL,
 GUELTIG_VON, GUELTIG_BIS)
VALUES
  (72, '150000 kbit/s', 10072, 'DOWNSTREAM',
   150000, '150000', '150000', 'Downstream 150.000 kbit/s',
   '0', '0', '0', '1', '0', '1',
   TO_DATE('01/11/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'));

-- techn. Leistung 15 Mbit/s Upstream
INSERT INTO T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP,
 LONG_VALUE, STR_VALUE, PROD_NAME_STR, DESCRIPTION,
 DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL,
 GUELTIG_VON, GUELTIG_BIS)
VALUES
  (73, '15000 kbit/s', 10073, 'UPSTREAM',
   15000, '15000', '15000', 'Upstream 15.000 kbit/s',
   '0', '0', '0', '1', '0', '1',
   TO_DATE('01/11/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'));
------------------------------------------------------------------------------


---- KOMBI - techn. Leistungen fuer FTTX Glasfaser Down-Upstream 150/15 ------
-- KOMBI Leistung 150_VOIP_IPv6
INSERT INTO T_TECH_LEISTUNG
(ID, NAME, TYP, EXTERN_LEISTUNG__NO, PROD_NAME_STR, DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS)
VALUES
  (496, '150_VOIP_IPv6', 'KOMBI', 10216, ' ',
   '0', '0', '0', '0', '0', '1',
   TO_DATE('01/11/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'));

-- MGA-Leistung, Downstream 150 Mbit (ID 72), Upstream 15 Mbit (ID 73)
INSERT INTO T_TECH_LEISTUNG
(ID, NAME, TYP, EXTERN_LEISTUNG__NO,
 PROD_NAME_STR, DESCRIPTION,
 DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL,
 GUELTIG_VON, GUELTIG_BIS)
VALUES
  (417, 'FTTX_GF_150/15_MGA', 'KOMBI', 10217,
   ' ', 'Kombi-Leistung, fuehrt zu: 150 MBit, 15 MBit, VOIP_MGA, Endgeraeteport',
   '0', '0', '0', '0', '0', '1',
   TO_DATE('01/11/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'));

-- TK-Leistung, Downstream 150 Mbit (ID 72), Upstream 15 Mbit (ID 73)
INSERT INTO T_TECH_LEISTUNG
(ID, NAME, TYP, EXTERN_LEISTUNG__NO, PROD_NAME_STR, DESCRIPTION, DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS)
VALUES
  (418, 'FTTX_GF_150/15_TK', 'KOMBI', 10218,
   ' ', 'Kombi-Leistung, fuehrt zu: 150 MBit, 15 MBit, VOIP_TK, Endgeraeteport',
   '0', '0', '0', '0', '0', '1',
   TO_DATE('01/11/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'));
------------------------------------------------------------------------------


---- Mapping der 150/50 Mbit/s Leistungen fuer Premium GF (PROD_ID = 540) -----
-- 150MB Down, FFTX_GF_150/15_MGA, default = true, first version
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 72, 417, '1', 0);

-- 15MB UP, FFTX_GF_150/15_MGA, default = true, first version
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 73, 417, '1', 0);

-- 150MB Down, FFTX_GF_150/15_TK, default = true, first version
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 72, 418, '1', 0);

-- 15MB UP, FFTX_GF_150/15_TK, default = true, first version
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 73, 418, '1', 0);
------------------------------------------------------------------------------


---- Mapping der 150/50 Mbit/s Leistungen fuer FFTX_DSL (PROD_ID = 512)  ----
-- 150_VOIP_IPv6, TECH_LS_DEPENDENCY = null, default = false, first version
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 512, 496, NULL, '0', 0);

-- 150MB Down, TECH_LS_DEPENDENCY = null, default = false, first version
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 512, 72, NULL, '0', 0);

-- 150MB Down, 150_VOIP_IPv6, default = true, first version
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 512, 72, 496, '1', 0);

-- 15MB UP, 150MB Down, default = true, first version
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 512, 73, 72, '1', 0);

-- 15MB UP, 150_VOIP_IPv6, default = true, first version
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 512, 73, 496, '1', 0);

-- dynamische IPv6, 150_VOIP_IPv6, default = true, first version
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 512, 57, 496, '1', 0);
------------------------------------------------------------------------------


---- Mapping der 150/50 Mbit/s Leistungen fuer FFTX_DSL + FON (PROD_ID = 513)  ----
-- 150_VOIP_IPv6, TECH_LS_DEPENDENCY = null, default = false, first version
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 513, 496, NULL, '0', 0);

-- 150MB Down, TECH_LS_DEPENDENCY = null, default = false, first version
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 513, 72, NULL, '0', 0);

-- 150MB Down, 150_VOIP_IPv6, default = true, first version
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 513, 72, 496, '1', 0);

-- 15MB UP, 150MB Down, default = true, first version
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 513, 73, 72, '1', 0);

-- 15MB UP, 150_VOIP_IPv6, default = true, first version
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 513, 73, 496, '1', 0);

-- dynamische IPv6, 150_VOIP_IPv6, default = true, first version
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 513, 57, 496, '1', 0);
------------------------------------------------------------------------------