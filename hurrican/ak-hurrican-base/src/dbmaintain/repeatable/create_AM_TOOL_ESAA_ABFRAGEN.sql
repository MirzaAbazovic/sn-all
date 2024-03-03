------------------------------------------------------------
-- KUP 329
-- create Basis-View für die AM Tool Abfrage
------------------------------------------------------------

CREATE OR REPLACE FUNCTION f_tbs_datei_2_ts_datum (cTBS_DATEI VARCHAR2)
   RETURN VARCHAR2 DETERMINISTIC IS  cret VARCHAR2(20);
BEGIN

   cret := to_char(to_date(substr(cTBS_DATEI,5,12),'yyyymmddhh24mi'),'yyyymmdd');
   return cret;

   -- fm 20.03.07 EXCEPTION neu
EXCEPTION
   WHEN NO_DATA_FOUND
   THEN
      NULL;
   WHEN OTHERS
   THEN
      -- Consider logging the error and then re-raise
      --NULL;
      return '*** Fehler ***';
END f_tbs_datei_2_ts_datum;
/

CREATE OR REPLACE FUNCTION f_tbs_datei_2_ts_zeit (cTBS_DATEI VARCHAR2)
   RETURN VARCHAR2 DETERMINISTIC IS  cret VARCHAR2(20);
BEGIN

   cret := to_char(to_date(substr(cTBS_DATEI,5,12),'yyyymmddhh24mi'),'hh24:mi:ss');
   return cret;

   -- fm 20.03.07 EXCEPTION neu
EXCEPTION
   WHEN NO_DATA_FOUND
   THEN
      NULL;
   WHEN OTHERS
   THEN
      -- Consider logging the error and then re-raise
      --NULL;
      return '*** Fehler ***';
END f_tbs_datei_2_ts_zeit;
/


CREATE OR REPLACE FUNCTION f_tbs_datei_2_ts (cTBS_DATEI VARCHAR2)
   RETURN VARCHAR2 DETERMINISTIC IS  cret VARCHAR2(20);
BEGIN

   cret := to_char(to_date(substr(cTBS_DATEI,5,12),'yyyymmddhh24mi'),'yyyymmdd hh24:mi:ss');
   return cret;

   -- fm 20.03.07 EXCEPTION neu
EXCEPTION
   WHEN NO_DATA_FOUND
   THEN
      NULL;
   WHEN OTHERS
   THEN
      -- Consider logging the error and then re-raise
      --NULL;
      return '*** Fehler ***';
END f_tbs_datei_2_ts;
/



------------------------------------------------------------
-- TAL Abfrage Rückmeldung OK
------------------------------------------------------------

CREATE OR REPLACE FORCE VIEW V_KONTR_ESAA_RM_OK AS
SELECT 
    A.TBS_TS,
    A.B005_2,
    A.B005_3,
    A.B005_4,
    A.TBS_ID,
    A.B017_2,
    A.TBS_TS_SND,
    A.TFE_KLASSE,
    A.B002_2,
    A.B002_4,
    A.B001_2,
    A.B001_4,
    A.B001_5,
    A.B001_6,
    A.B015_2_SND,
    A.B015_3_SND,
    A.anz_arbeitstage
FROM 
    mnetcall.VHUR_TTALBEST_B005_I1_OK@kupvis a
/
GRANT SELECT ON V_KONTR_ESAA_RM_OK TO R_HURRICAN_USER
/


------------------------------------------------------------
-- TAL Rückmeldung fehlt und Kündigungen
------------------------------------------------------------

CREATE OR REPLACE FORCE VIEW V_KONTR_ESAA_RM_ERR_O_KUEND AS
SELECT 
    A.TBS_TS,
    A.B005_2,
    A.B005_3,
    A.B005_4,
    A.TBS_ID,
    A.B017_2,
    A.TBS_TS_SND,
    A.TFE_KLASSE,
    A.B002_2,
    A.B002_4,
    A.B001_2,
    A.B001_4,
    A.B001_5,
    A.B001_6,
    A.B015_2_SND,
    A.B015_3_SND,
    A.B001_2_RM,
    A.anz_arbeitstage
FROM 
    mnetcall.VHUR_TTALBEST_B005_I1_ERR@kupvis a
/
GRANT SELECT ON V_KONTR_ESAA_RM_ERR_O_KUEND TO R_HURRICAN_USER
/

------------------------------------------------------------
-- TAL Rückmeldung fehlt Abfrage
------------------------------------------------------------

CREATE OR REPLACE FORCE VIEW V_KONTR_ESAA_RM_FEHLT
(
   CB_ID,
   LBZ,
   ESAA_VORGANG,
   TS_DATUM,
   TS_ZEIT,
   TBS_LEVEL,
   TBS_ID,
   TBS_TBV_ID,
   TBS_AUF_ID,
   TBS_LTG_ID,
   TBS_SRC_ID,
   TBS_TBS_ID,
   TBS_TBS_FIRST_ID,
   TBS_STATUS,
   TBS_DATEI,
   TBS_SENDER,
   TBS_RECIPIENT,
   TBS_SENDEVERSUCH,
   B001_ID,
   B001_2,
   B001_4,
   B001_5,
   B001_6,
   B005_ID,
   B005_2,
   B005_3,
   B005_4,
   TFE_ID,
   B002_ID,
   B002_2
)
AS
   SELECT c.cb_id,
          C.LBZ,
          e.str_value AS ESAA_Vorgang,
          f_tbs_datei_2_ts_datum (a.TBS_DATEI) AS ts_datum,
          f_tbs_datei_2_ts_zeit (a.TBS_DATEI) AS ts_zeit,
          a."TBS_LEVEL",
          a."TBS_ID",
          a."TBS_TBV_ID",
          a."TBS_AUF_ID",
          a."TBS_LTG_ID",
          a."TBS_SRC_ID",
          a."TBS_TBS_ID",
          a."TBS_TBS_FIRST_ID",
          a."TBS_STATUS",
          a."TBS_DATEI",
          a."TBS_SENDER",
          a."TBS_RECIPIENT",
          a."TBS_SENDEVERSUCH",
          a."B001_ID",
          a."B001_2",
          a."B001_4",
          a."B001_5",
          a."B001_6",
          a."B005_ID",
          a."B005_2",
          a."B005_3",
          a."B005_4",
          a."TFE_ID",
          a."B002_ID",
          a."B002_2"
   FROM T_REFERENCE e,
        T_CB_USECASE d,
        t_carrierbestellung c,
        T_CB_VORGANG b,
        mnetcall.V1TTALBESTELLUNG_S_MAX_I1@kupvis a
   WHERE a.TBS_SRC_ID = 0 AND
         a.TBS_STATUS <> 0 AND
         a.TBS_TBS_ID IS NULL AND
         D.REFERENCE_ID = e.id(+) AND
         a.tbs_tbv_id = d.exm_tbv_id(+) AND
         b.cb_id = c.cb_id(+) AND
         a.tbs_id = b.exm_id(+)
/
GRANT SELECT ON V_KONTR_ESAA_RM_FEHLT TO R_HURRICAN_USER
/

------------------------------------------------------------
-- TAL Übersicht Abfrage
------------------------------------------------------------

CREATE OR REPLACE FORCE VIEW V_KONTR_ESAA_TAL_UEBERSICHT AS
   SELECT 
          a.tbs_id,
          c.cb_id,
          c.LBZ,
          e.str_value AS ESAA_Vorgang,
          a.ts_datum,
          a.ts_zeit,
          a.B001_2,
          a.B001_4,
          a.B001_5,
          a.B001_6,
          a.B005_2,
          a.B005_3,
          a.B005_4,
          a.tfe_klasse,
          a.B002_2,
          a.B009_3,
          a.B009_5
   FROM T_REFERENCE e,
        T_CB_USECASE d,
        t_carrierbestellung c,
        T_CB_VORGANG b,
        mnetcall.VHUR_TTALBESTELLUNG_S_MAX_I1@kupvis a
   WHERE D.REFERENCE_ID = e.id(+) AND
         a.tbs_tbv_id = d.exm_tbv_id(+) AND
         b.cb_id = c.cb_id(+) AND
         a.tbs_tbs_first_id = b.exm_id(+)
/
GRANT SELECT ON V_KONTR_ESAA_TAL_UEBERSICHT TO R_HURRICAN_USER
/
