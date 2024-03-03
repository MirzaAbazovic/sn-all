CREATE OR REPLACE FUNCTION f_str_suchname (varSTR_NAME VARCHAR2)
   RETURN VARCHAR2 DETERMINISTIC IS  cret VARCHAR2(50);
BEGIN
cret := 
translate(
replace(
replace(
replace(
replace(
replace(
replace(
replace(
replace(
replace(
replace(
replace(
replace(
upper(
varSTR_NAME
),'DOKTOR','DR'
),'ST.','SANKT'
),'PROFESSOR','PROF'
),'STRASSE','STR'
),'STRAßE','STR'
),'STRA¿E','STR'
),' STR','STR'
),'ß','SS'
),'¿','Ü'
),'Ü','UE'
),'Ö','OE'
),'Ä','AE'
),
'0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. ,',
'0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'
);
   RETURN cret;
END f_str_suchname;
/
BEGIN
    execute immediate('DROP INDEX I2T_STRASSENLISTE');
EXCEPTION
   WHEN OTHERS THEN
      NULL;
END;
/
CREATE INDEX I2T_STRASSENLISTE ON T_STRASSENLISTE
(   
  ORT
)
NOLOGGING 
TABLESPACE T_HURRICAN
/

BEGIN
    execute immediate('DROP INDEX I3T_STRASSENLISTE');
EXCEPTION
   WHEN OTHERS THEN
      NULL;
END;
/
CREATE INDEX I3T_STRASSENLISTE ON T_STRASSENLISTE
(   
  STRASSE
)
NOLOGGING 
TABLESPACE T_HURRICAN
/
CREATE OR REPLACE PROCEDURE P_LK_UPD1
IS
BEGIN
   DECLARE
      INSERT_COUNT       NUMBER := 1;

      iGEO_CITY_NO_1     NUMBER;
      iGEO_CITY_NO_2     NUMBER;

      iGEO_CITY_NO_NEW   NUMBER;
      iGEO_CITY_NO_OLD   NUMBER;

      i                  NUMBER;

      cCITY_NEW          VARCHAR2 (250);

      cUSER              VARCHAR2 (50);

      CURSOR c1
      IS

  SELECT   a.sl_id, a.ONKZ, b.ONKZ AS onkz_neu
    FROM   T_HVT_GRUPPE b,
       T_STRASSENLISTE a
   WHERE   a.HVT_GRUPPE_ID = b.HVT_GRUPPE_ID AND NVL (a.onkz, 0) <> b.onkz
ORDER BY   a.sl_id;

      r1                 c1%ROWTYPE;
   BEGIN
      FOR r1 IN c1
      LOOP
         UPDATE   T_STRASSENLISTE A
            SET   A.onkz = r1.onkz_neu
          WHERE   A.SL_ID = r1.SL_ID;

         COMMIT;
      END LOOP;

      COMMIT;
   END;
END;
/


---------------------------------------------------------
-- exec
---------------------------------------------------------
DECLARE
   i   NUMBER := 1;
BEGIN
   FOR i IN 1 .. 1
   LOOP
      P_LK_UPD1;
      COMMIT;
   END LOOP;
END;
/


CREATE OR REPLACE PROCEDURE P_LK_UPD2
IS
BEGIN
   DECLARE
      INSERT_COUNT       NUMBER := 1;

      iGEO_CITY_NO_1     NUMBER;
      iGEO_CITY_NO_2     NUMBER;

      iGEO_CITY_NO_NEW   NUMBER;
      iGEO_CITY_NO_OLD   NUMBER;

      i                  NUMBER;

      cCITY_NEW          VARCHAR2 (250);

      cUSER              VARCHAR2 (50);

      CURSOR c1
      IS

           SELECT   MIN (b.SL_ID) AS SL_ID_1,
                    MAX (b.SL_ID) AS SL_ID_2,
                    b.HVT_GRUPPE_ID,
--                  b.ONKZ,
                    b.ORT,
                    b.STRASSE,
                    NVL (b.ORTSZUSATZ, 'X'),
                    NVL (b.ABSCHNITT, 'X'),
                    b.plz
             FROM   T_STRASSENLISTE b
         GROUP BY   b.HVT_GRUPPE_ID,
--                  b.ONKZ,
                    b.ORT,
                    b.STRASSE,
                    NVL (b.ORTSZUSATZ, 'X'),
                    NVL (b.ABSCHNITT, 'X'),
                    b.plz
           HAVING   COUNT ( * ) > 1;

      r1                 c1%ROWTYPE;
   BEGIN
      FOR r1 IN c1
      LOOP
         UPDATE   T_ENDSTELLE A
            SET   A.SL_ID = r1.SL_ID_1
          WHERE   A.SL_ID = r1.SL_ID_2;


         DELETE FROM   T_STRASSENLISTE a
               WHERE   a.SL_ID = r1.SL_ID_2;

         COMMIT;
      END LOOP;

      COMMIT;
   END;
END;
/


---------------------------------------------------------
-- exec
---------------------------------------------------------
DECLARE
   i   NUMBER := 1;
BEGIN
   FOR i IN 1 .. 10
   LOOP
      P_LK_UPD2;
      COMMIT;
   END LOOP;
END;
/

drop PROCEDURE P_LK_UPD2
/


-----------------------------------------------
CREATE OR REPLACE PROCEDURE P_LK_UPD3
IS
BEGIN
   DECLARE
      INSERT_COUNT       NUMBER := 1;

      iGEO_CITY_NO_1     NUMBER;
      iGEO_CITY_NO_2     NUMBER;

      iGEO_CITY_NO_NEW   NUMBER;
      iGEO_CITY_NO_OLD   NUMBER;

      i                  NUMBER;

      cCITY_NEW          VARCHAR2 (250);

      cUSER              VARCHAR2 (50);

      CURSOR c1
      IS

           SELECT   MIN (b.SL_ID) AS SL_ID_1,
                    MAX (b.SL_ID) AS SL_ID_2,
                    b.HVT_GRUPPE_ID,
--                  b.ONKZ,
                    b.ORT,
                    max(b.strasse) as strasse_1,
                    min(b.strasse) as strasse_2,
                    f_str_suchname(b.STRASSE),
                    NVL (b.ORTSZUSATZ, 'X'),
                    NVL (b.ABSCHNITT, 'X'),
                    b.plz
             FROM   T_STRASSENLISTE b
         GROUP BY   b.HVT_GRUPPE_ID,
--                  b.ONKZ,
                    b.ORT,
                    f_str_suchname(b.STRASSE),
                    NVL (b.ORTSZUSATZ, 'X'),
                    NVL (b.ABSCHNITT, 'X'),
                    b.plz
           HAVING   COUNT ( * ) > 1;

      r1                 c1%ROWTYPE;
   BEGIN
      FOR r1 IN c1
      LOOP
         UPDATE   T_ENDSTELLE A
            SET   A.SL_ID = r1.SL_ID_1
          WHERE   A.SL_ID = r1.SL_ID_2;


         DELETE FROM   T_STRASSENLISTE a
               WHERE   a.SL_ID = r1.SL_ID_2;

         COMMIT;
      END LOOP;

      COMMIT;
   END;
END;
/

---------------------------------------------------------
-- exec
---------------------------------------------------------
DECLARE
   i   NUMBER := 1;
BEGIN
   FOR i IN 1 .. 10
   LOOP
      P_LK_UPD3;
      COMMIT;
   END LOOP;
END;
/

drop PROCEDURE P_LK_UPD3
/

BEGIN
    execute immediate('drop INDEX I1T_STRASSENLISTE');
EXCEPTION
   WHEN OTHERS THEN
      NULL;
END;
/
CREATE UNIQUE INDEX I1T_STRASSENLISTE ON T_STRASSENLISTE
(   
  HVT_GRUPPE_ID,
--  ONKZ,
  ORT,
  ORTSZUSATZ,
  STRASSE,
  ABSCHNITT,
  PLZ
)
NOLOGGING 
TABLESPACE T_HURRICAN
/

BEGIN
    execute immediate('drop INDEX I1FCNT_STRASSENLISTE');
EXCEPTION
   WHEN OTHERS THEN
      NULL;
END;
/
CREATE UNIQUE INDEX I1FCNT_STRASSENLISTE ON T_STRASSENLISTE
(   
  HVT_GRUPPE_ID,
--  ONKZ,
  ORT,
  ORTSZUSATZ,
  f_str_suchname(STRASSE),
  ABSCHNITT,
  PLZ
)
NOLOGGING 
TABLESPACE T_HURRICAN
/

-- BEGIN 
--   sys.utl_recomp.recomp_serial();
--   COMMIT; 
-- END; 
CREATE OR REPLACE VIEW VINS_T_STRASSENLISTE AS
SELECT
A.SL_ID,
CAST(NULL AS VARCHAR2(20)) AS comp,
         A.ONKZ,
         A.ORT,
         A.ORTSZUSATZ,
         A.STRASSE,
         A.ABSCHNITT,
         A.BEMERKUNG,
         A.PLZ,
         A.ENTFERNUNG_DEC,
         A.LAST_CHANGE
  FROM  T_STRASSENLISTE A
/

CREATE OR REPLACE TRIGGER TRG_VINS_T_STRASSENLISTE
   INSTEAD OF INSERT
   ON VINS_T_STRASSENLISTE
   FOR EACH ROW
DECLARE
      iHVT_GRUPPE_ID   T_STRASSENLISTE.HVT_GRUPPE_ID%TYPE;
      iseq_SL_ID NUMBER(11);
BEGIN
   BEGIN
      iHVT_GRUPPE_ID := NULL;
      iseq_SL_ID := NULL;

      SELECT   HG.HVT_GRUPPE_ID
        INTO   iHVT_GRUPPE_ID
        FROM               T_HVT_GRUPPE hg
                        INNER JOIN
                           T_HVT_STANDORT hs
                        ON hs.HVT_GRUPPE_ID = hg.HVT_GRUPPE_ID
                     LEFT JOIN
                        T_REFERENCE r
                     ON hs.STANDORT_TYP_REF_ID = r.ID
                  LEFT JOIN
                     T_REFERENCE ru
                  ON r.UNIT_ID = ru.ID
               LEFT JOIN
                  T_NIEDERLASSUNG nl
               ON hg.NIEDERLASSUNG_ID = nl.ID
       WHERE   hg.ONKZ || '_' || hs.ASB || '_' || hs.KVZ_NUMMER = :NEW.comp
               AND hs.ASB IS NOT NULL
               AND ru.STR_VALUE IS NOT NULL
               AND hs.GUELTIG_BIS > SYSDATE;

      SELECT S_T_STRASSENLISTE_0.NEXTVAL INTO iseq_SL_ID FROM DUAL;

      INSERT INTO T_STRASSENLISTE A (A.SL_ID,
                                     A.HVT_GRUPPE_ID,
                                     A.ONKZ,
                                     A.ORT,
                                     A.ORTSZUSATZ,
                                     A.STRASSE,
                                     A.ABSCHNITT,
                                     A.BEMERKUNG,
                                     A.PLZ,
                                     A.ENTFERNUNG_DEC,
                                     A.LAST_CHANGE)
        VALUES   (iseq_SL_ID,
                  iHVT_GRUPPE_ID,
                  :NEW.ONKZ,
                  :NEW.ORT,
                  :NEW.ORTSZUSATZ,
                  :NEW.STRASSE,
                  :NEW.ABSCHNITT,
                  :NEW.BEMERKUNG,
                  :NEW.PLZ,
                  :NEW.ENTFERNUNG_DEC,
                  :NEW.LAST_CHANGE);
   EXCEPTION
      WHEN NO_DATA_FOUND
      THEN
         NULL;
      WHEN OTHERS
      THEN
         NULL;
   END;
END;
/
commit
/

