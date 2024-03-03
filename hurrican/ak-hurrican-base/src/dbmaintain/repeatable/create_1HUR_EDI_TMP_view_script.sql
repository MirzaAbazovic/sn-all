BEGIN
   EXECUTE IMMEDIATE 'DROP INDEX I1FCN_T_CARRIERBESTELLUNG';
EXCEPTION
   WHEN OTHERS
   THEN
      NULL;
END;
/

BEGIN
   DECLARE
      csql   VARCHAR2 (2000);
   BEGIN
      csql :=
         '
CREATE INDEX I1FCN_T_CARRIERBESTELLUNG ON T_CARRIERBESTELLUNG
(SUBSTR(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(LBZ,''/0000'',''/''),''/000'',''/''),''/00'',''/''),''/0'',''/''),''/89001'',''/89000''),''/89002'',''/89000''),''/89003'',''/89000''),''/89004'',''/89000''),''/89005'',''/89000''),''/89006'',''/89000''),''/89007'',''/89000''),''/89008'',''/89000''),''/89009'',''/89000''),''000/'',''/''),''00/'',''/''),''0/'',''/''),1,40))
NOLOGGING
TABLESPACE T_HURRICAN
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL';

      DBMS_OUTPUT.PUT_LINE ('csql = ' ||
                            csql);

      EXECUTE IMMEDIATE csql;
   EXCEPTION
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('SQLERRM = ' ||
                               SQLERRM);
         NULL;
   END;
END;
/

CREATE OR REPLACE FORCE VIEW V1EDI_T_HVT_2_NL
(
   HVT_GRUPPE_ID,
   ONKZ,
   ASB,
   KOSTENSTELLE,
   NIEDERLASSUNG,
   COMP
)
AS
   SELECT a.hvt_gruppe_id,
          b.onkz,
          a.asb,
          b.KOSTENSTELLE,
          c.text AS Niederlassung,
          b.onkz ||
          '_' ||
          a.asb
             AS comp
   FROM T_NIEDERLASSUNG c,
        t_hvt_gruppe b,
        t_hvt_standort a
   WHERE a.asb IS NOT NULL AND
         b.NIEDERLASSUNG_ID = c.id AND
         a.hvt_gruppe_ID = b.hvt_gruppe_ID;

CREATE OR REPLACE FORCE VIEW V1EDI_T_ONKZ_2_NL AS
   SELECT RPAD (a.onkz,
                5,
                0)
             AS onkzlen5,
          a.Niederlassung
   FROM (SELECT SUBSTR (b.onkz,
                        2)
                   AS onkz,
                MIN (c.text) AS Niederlassung
         FROM T_NIEDERLASSUNG c,
              t_hvt_gruppe b,
              t_hvt_standort a
         WHERE b.onkz <> '0' AND
               a.asb IS NOT NULL AND
               b.NIEDERLASSUNG_ID = c.id AND
               a.hvt_gruppe_ID = b.hvt_gruppe_ID
         GROUP BY SUBSTR (b.onkz,
                          2)) a;
-----------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------
-- and limit to DTAG
-----------------------------------------------------------------------------------------
CREATE OR REPLACE FORCE VIEW V1T_CARRIERBESTELLUNG as
select
SUBSTR(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(A.LBZ,'/0000','/'),'/000','/'),'/00','/'),'/0','/'),'/89001','/89000'),'/89002','/89000'),'/89003','/89000'),'/89004','/89000'),'/89005','/89000'),'/89006','/89000'),'/89007','/89000'),'/89008','/89000'),'/89009','/89000'),'000/','/'),'00/','/'),'0/','/'),1,40)
             AS LBZ_COMP,
a.*
from
T_CARRIERBESTELLUNG a
where
a.carrier_id = 12;

------------------------------------------------------------------------------------------
-- wegen Nutzungsänderungen kann eine LB Z doppelt in der T_CARRIERBESTELLUNG stehen
-- eine mit gekündigt, die andere als aktiv, deshalb mig Tabelle g verlinken
--
-- und reduiterung auf genau eine (die letze) Auftragsnummer
--
------------------------------------------------------------------------------------------
--
--
--
-- man koennte immer eine INNER JOIN nehmen
-- aber die Strassen und HVT Zuordnungen fehlen manchmal
------------------------------------------------------------------------------------------

CREATE OR REPLACE FORCE VIEW V1EDI_T_CARRIERBESTELLUNG AS
   SELECT d.LBZ_COMP,
          d.LBZ,
          d.VTRNR,
          a.PRODAK_ORDER__NO,
          a.auftrag_id,
          d.BEREITSTELLUNG_AM,
          d.KUENDBESTAETIGUNG_CARRIER,
          f.onkz,
          e.asb,
          MAX (a.bemerkungen) AS bemerkungen
   FROM                   T_AUFTRAG_DATEN a
                       INNER JOIN
                          T_AUFTRAG_TECHNIK b
                       ON a.AUFTRAG_ID = b.AUFTRAG_ID
                    INNER JOIN
                       T_ENDSTELLE c
                    ON b.AT_2_ES_ID = c.ES_GRUPPE
                 INNER JOIN
                    V1T_CARRIERBESTELLUNG d
                 ON c.CB_2_ES_ID = d.CB_2_ES_ID
              LEFT OUTER JOIN
                 t_hvt_standort e
              ON e.HVT_ID_STANDORT = c.HVT_ID_STANDORT
           LEFT OUTER JOIN
              t_hvt_gruppe f
           ON f.hvt_gruppe_ID = e.hvt_gruppe_ID
        INNER JOIN
           (SELECT MAX (gg.bestellt_am) AS bestellt_am,
                   GG.LBZ_COMP
            FROM V1T_CARRIERBESTELLUNG gg
            GROUP BY GG.LBZ_COMP) g
        ON g.LBZ_COMP = d.LBZ_COMP AND
           g.bestellt_am = d.bestellt_am
   WHERE                                                                    --
         ----------------------------------------------
         --      d.lbz LIKE '96W/82820/82820/%1576' AND
         ----------------------------------------------
         --      d.lbz LIKE '96U/82100/82100/7055' AND
         ----------------------------------------------
         d.lbz NOT IN ('.', '0') AND
         a.GUELTIG_BIS > SYSDATE AND
         a.STATUS_ID < 10000 AND
         a.STATUS_ID NOT IN (1150, 3400) AND
         b.GUELTIG_BIS > SYSDATE
   GROUP BY d.LBZ_COMP,
            d.LBZ,
            d.VTRNR,
            a.PRODAK_ORDER__NO,
            a.auftrag_id,
            d.BEREITSTELLUNG_AM,
            d.KUENDBESTAETIGUNG_CARRIER,
            f.onkz,
            e.asb;


CREATE OR REPLACE FORCE VIEW V2EDI_T_CARRIERBESTELLUNG AS
   SELECT SUBSTR (
             REPLACE (
                REPLACE (
                   REPLACE (
                      REPLACE (
                         REPLACE (
                            REPLACE (
                               REPLACE (
                                  REPLACE (
                                     REPLACE (
                                        REPLACE (
                                           REPLACE (
                                              REPLACE (
                                                 REPLACE (
                                                    REPLACE (
                                                       REPLACE (
                                                          REPLACE (A.LBZ,
                                                                   '/0000',
                                                                   '/'),
                                                          '/000',
                                                          '/'
                                                       ),
                                                       '/00',
                                                       '/'
                                                    ),
                                                    '/0',
                                                    '/'
                                                 ),
                                                 '/89001',
                                                 '/89000'
                                              ),
                                              '/89002',
                                              '/89000'
                                           ),
                                           '/89003',
                                           '/89000'
                                        ),
                                        '/89004',
                                        '/89000'
                                     ),
                                     '/89005',
                                     '/89000'
                                  ),
                                  '/89006',
                                  '/89000'
                               ),
                               '/89007',
                               '/89000'
                            ),
                            '/89008',
                            '/89000'
                         ),
                         '/89009',
                         '/89000'
                      ),
                      '000/',
                      '/'
                   ),
                   '00/',
                   '/'
                ),
                '0/',
                '/'
             ),
             1,
             40
          )
             AS LBZ_COMP,
          a.LBZ,
          a.VTRNR,
          a.PRODAK_ORDER__NO,
          a.AUFTRAG_ID,
          a.BEREITSTELLUNG_AM,
          a.KUENDBESTAETIGUNG_CARRIER,
          a.ONKZ,
          a.ASB,
          a.BEMERKUNGEN
   FROM V1EDI_T_CARRIERBESTELLUNG A;

----------------------------------------------------------------------------------
-- views on tmp table
----------------------------------------------------------------------------------

CREATE OR REPLACE FORCE VIEW V3EDI_T_CARRIERBESTELLUNG AS
   SELECT b.LBZ,
          b.VTRNR,
          c.niederlassung,
          b.PRODAK_ORDER__NO,
          b.AUFTRAG_ID,
          b.BEREITSTELLUNG_AM,
          b.KUENDBESTAETIGUNG_CARRIER,
          b.ONKZ,
          b.ASB,
          A.EDT_ID,
          --       A.EDF_MONAT,
          A.EDF_RECHNUNGSDATUM,
          --       A.EDF_BETRAG_MOA9,
          --       A.EDF_BETRAG_MOA77,
          --       A.EDF_BETRAG_MOA79,
          --       A.EDF_BETRAG_MOA124,
          --       A.EDF_BETRAG_MOA125,
          --       A.EDF_NAME,
          --       A.EDF_TEXT1,
          --       A.EDF_TEXT2,
          --       A.T_DTAG_RECHNUNG_RECH_ID,
          --       A.EDF_ABRECHNUNG_ART,
          --       A.EDF_DATEITYP,
          --       A.EDF_ABRECHNUNG_BIS,
          --       A.EDT_EDF_ID,
          --       A.EDT_EDA_ID,
          --       A.EDT_ETA_ID,
          --       A.EDT_ETY_ID,
          --       A.EDT_EAL_ID,
          A.EAL_NR_ELFE,
          A.EDT_VON,
          A.EDT_BIS,
          A.EDT_ANZ,
          --       A.EDT_BETRAG_POS,
          A.EDT_BETRAG,
          --       A.LBZ_COMP,
          A.EDT_LEITUNGSNR,
          --       A.EDT_VERTRAGSNR,
          A.EDT_REFERENZ,
          A.EDT_NAME,
          A.EDT_TEXT1,
          A.EDT_TEXT2,
          A.EDT_LEITUNGSNR_ORI,
          A.EDT_ENDSTELLE_A,
          A.EDT_ENDSTELLE_B,
          A.EDT_EXTERNE_AUFTRAGSNUMMER,
          A.EDT_STOERUNGSNUMMER,
          A.EDT_STOERUNGSDATUM,
          A.EDT_GUTSCHRIFT_VERTRAGSNUMMER,
          A.EDT_MATERIALNUMMER,
          A.EDT_KONDITIONSID,
          A.EDT_VERTRAGSNUMMER,
          A.T_DTAG_TAL_RECH_ID,
          A.T_DTAG_TAL_ID,
          A.EAL_KONDITIONS_ID,
          --     A.EAL_ID,
          A.EAL_NR,
          A.EAL_RECHNUNGSTEXT,
          A.EAL_TEXT,
          --     , A.LBZ_ONKZ
          CAST (SUBSTR (b.bemerkungen,
                                -200) AS VARCHAR2 (200))
           bemerkungen
   FROM V1EDI_T_HVT_2_NL C,
        V2EDI_T_CARRIERBESTELLUNG B,
        T_EDI_KUP_TMP A
   WHERE                                                     -- rownum = 1 and
         -- b.KUENDBESTAETIGUNG_CARRIER is NOT NULL and
         -- NVL(b.KUENDBESTAETIGUNG_CARRIER,sysdate+1) <= add_months(sysdate,-1) and
         -- b.edt_von > b.KUENDBESTAETIGUNG_CARRIER and
         -------------------------------------------------------------------
         -- mindestens 2 Monat alte Kündigung
         -- add_months(b.edt_von,-2) > b.KUENDBESTAETIGUNG_CARRIER and
         -------------------------------------------------------------------
         -- mindestens 1 Monat alte Kündigung
         ADD_MONTHS (a.edt_von,
                     -2) > b.KUENDBESTAETIGUNG_CARRIER AND
         -------------------------------------------------------------------
         a.edt_betrag > 0 AND
         b.ONKZ ||
         '_' ||
         b.ASB = c.comp(+) AND
         a.LBZ_COMP = B.LBZ_COMP
   ORDER BY b.KUENDBESTAETIGUNG_CARRIER,
            b.LBZ;

grant select on V3EDI_T_CARRIERBESTELLUNG to R_HURRICAN_USER;

------------------------------------------------------------------------------
-- fehlende Augsburg /
-- 19.07.2010 (A+M)
------------------------------------------------------------------------------

CREATE OR REPLACE FORCE VIEW V4EDI_T_CARRIERBESTELLUNG_A AS
   SELECT c.niederlassung,
          A.EDT_ID,
          --       A.EDF_MONAT,
          A.EDF_RECHNUNGSDATUM,
          --       A.EDF_BETRAG_MOA9,
          --       A.EDF_BETRAG_MOA77,
          --       A.EDF_BETRAG_MOA79,
          --       A.EDF_BETRAG_MOA124,
          --       A.EDF_BETRAG_MOA125,
          --       A.EDF_NAME,
          --       A.EDF_TEXT1,
          --       A.EDF_TEXT2,
          --       A.T_DTAG_RECHNUNG_RECH_ID,
          --       A.EDF_ABRECHNUNG_ART,
          --       A.EDF_DATEITYP,
          --       A.EDF_ABRECHNUNG_BIS,
          --       A.EDT_EDF_ID,
          --       A.EDT_EDA_ID,
          --       A.EDT_ETA_ID,
          --       A.EDT_ETY_ID,
          --       A.EDT_EAL_ID,
          A.EAL_NR_ELFE,
          A.EDT_VON,
          A.EDT_BIS,
          A.EDT_ANZ,
          --       A.EDT_BETRAG_POS,
          A.EDT_BETRAG,
          --       A.LBZ_COMP,
          A.EDT_LEITUNGSNR,
          --       A.EDT_VERTRAGSNR,
          A.EDT_REFERENZ,
          A.EDT_NAME,
          A.EDT_TEXT1,
          A.EDT_TEXT2,
          A.EDT_LEITUNGSNR_ORI,
          A.EDT_ENDSTELLE_A,
          A.EDT_ENDSTELLE_B,
          A.EDT_EXTERNE_AUFTRAGSNUMMER,
          A.EDT_STOERUNGSNUMMER,
          A.EDT_STOERUNGSDATUM,
          A.EDT_GUTSCHRIFT_VERTRAGSNUMMER,
          A.EDT_MATERIALNUMMER,
          A.EDT_KONDITIONSID,
          A.EDT_VERTRAGSNUMMER,
          A.T_DTAG_TAL_RECH_ID,
          A.T_DTAG_TAL_ID,
          A.EAL_KONDITIONS_ID,
          --     A.EAL_ID,
          A.EAL_NR,
          A.EAL_RECHNUNGSTEXT,
          A.EAL_TEXT,
          CAST (SUBSTR (b.bemerkungen,
                                -200) AS VARCHAR2 (200))
           bemerkungen
   --     , A.LBZ_ONKZ
   FROM V1EDI_T_ONKZ_2_NL C,
        V2EDI_T_CARRIERBESTELLUNG B,
        T_EDI_KUP_TMP A
   WHERE -----------------------------------------------------------------------------
                                                         -- identifiy Augsburg
 -----------------------------------------------------------------------------
                                         --   c.niederlassung <> 'München' AND
                                                    --    a.edt_betrag > 0 AND
  a.LBZ_ONKZ = c.onkzlen5 AND
  a.LBZ_COMP = B.LBZ_COMP(+) AND
  b.LBZ_COMP IS NULL
   ORDER BY b.KUENDBESTAETIGUNG_CARRIER,
            b.LBZ;

grant select on V4EDI_T_CARRIERBESTELLUNG_A to R_HURRICAN_USER;

------------------------------------------------------------------------------
-- alle Nürnberg
------------------------------------------------------------------------------

CREATE OR REPLACE FORCE VIEW V4EDI_T_CARRIERBESTELLUNG_N as
SELECT c.niederlassung,
       A.EDT_ID,
--       A.EDF_MONAT,
       A.EDF_RECHNUNGSDATUM,
--       A.EDF_BETRAG_MOA9,
--       A.EDF_BETRAG_MOA77,
--       A.EDF_BETRAG_MOA79,
--       A.EDF_BETRAG_MOA124,
--       A.EDF_BETRAG_MOA125,
--       A.EDF_NAME,
--       A.EDF_TEXT1,
--       A.EDF_TEXT2,
--       A.T_DTAG_RECHNUNG_RECH_ID,
--       A.EDF_ABRECHNUNG_ART,
--       A.EDF_DATEITYP,
--       A.EDF_ABRECHNUNG_BIS,
--       A.EDT_EDF_ID,
--       A.EDT_EDA_ID,
--       A.EDT_ETA_ID,
--       A.EDT_ETY_ID,
--       A.EDT_EAL_ID,
       A.EAL_NR_ELFE,
       A.EDT_VON,
       A.EDT_BIS,
       A.EDT_ANZ,
--       A.EDT_BETRAG_POS,
       A.EDT_BETRAG,
--       A.LBZ_COMP,
       A.EDT_LEITUNGSNR,
--       A.EDT_VERTRAGSNR,
       A.EDT_REFERENZ,
       A.EDT_NAME,
       A.EDT_TEXT1,
       A.EDT_TEXT2,
       A.EDT_LEITUNGSNR_ORI,
       A.EDT_ENDSTELLE_A,
       A.EDT_ENDSTELLE_B,
       A.EDT_EXTERNE_AUFTRAGSNUMMER,
       A.EDT_STOERUNGSNUMMER,
       A.EDT_STOERUNGSDATUM,
       A.EDT_GUTSCHRIFT_VERTRAGSNUMMER,
       A.EDT_MATERIALNUMMER,
       A.EDT_KONDITIONSID,
       A.EDT_VERTRAGSNUMMER,
       A.T_DTAG_TAL_RECH_ID,
       A.T_DTAG_TAL_ID,
       A.EAL_KONDITIONS_ID,
--     A.EAL_ID,
       A.EAL_NR,
       A.EAL_RECHNUNGSTEXT,
       A.EAL_TEXT
--     , A.LBZ_ONKZ
FROM
     V1EDI_T_ONKZ_2_NL C,
     V2EDI_T_CARRIERBESTELLUNG B,
     T_EDI_KUP_TMP A
WHERE
--    a.edt_betrag > 0 AND
      a.LBZ_ONKZ = c.onkzlen5(+) and
---------------------------------------------
-- identify Nürnberg
---------------------------------------------
      c.onkzlen5 is NULL AND
      a.LBZ_COMP = B.LBZ_COMP(+) and
      b.LBZ_COMP is NULL
ORDER BY
      b.KUENDBESTAETIGUNG_CARRIER,
      b.LBZ;

grant select on V4EDI_T_CARRIERBESTELLUNG_N to R_HURRICAN_USER;

-----------------------------------------------------------------
-----------------------------------------------------------------
------------------------------------------------------------------
------------------------------------------------------------------
-- nur in HuR aktiv, aber fehlt auf der Rechnung
------------------------------------------------------------------
------------------------------------------------------------------

CREATE OR REPLACE FORCE VIEW V4KUP_T_CARRIERBESTELL_AKT_A AS
   SELECT c.niederlassung,
          A.LBZ_COMP,
          A.LBZ,
          A.VTRNR,
          A.PRODAK_ORDER__NO,
          A.AUFTRAG_ID,
          A.BEREITSTELLUNG_AM,
          --     A.KUENDBESTAETIGUNG_CARRIER,
          A.ONKZ,
          A.ASB,
          --     , A.LBZ_ONKZ
          CAST (SUBSTR (a.bemerkungen,
                                -200) AS VARCHAR2 (200))
           bemerkungen
   FROM V1EDI_T_HVT_2_NL C,
        T_EDI_KUP_TMP B,
        V2EDI_T_CARRIERBESTELLUNG A
   WHERE ---------------------------------------------------------------------------------------------------
                                                        -- willkürlicher Range
  (TO_CHAR (A.BEREITSTELLUNG_AM,
            'yyyymm') > 200501 AND
   A.BEREITSTELLUNG_AM < ADD_MONTHS (SYSDATE,
                                     -2) OR
   TO_CHAR (A.BEREITSTELLUNG_AM,
            'yyyymm') > 201201) AND
  ---------------------------------------------------------------------------------------------------
  -- warum gibts andere
  SUBSTR (A.LBZ_COMP,
          1,
          2) = '96' AND
  ---------------------------------------------------------------------------------------------------
  A.KUENDBESTAETIGUNG_CARRIER IS NULL AND
  a.ONKZ ||
  '_' ||
  a.ASB = c.comp(+) AND
  a.LBZ_COMP = B.LBZ_COMP(+) AND
  b.LBZ_COMP IS NULL
   ORDER BY a.LBZ;

grant select on V4KUP_T_CARRIERBESTELL_AKT_A to R_HURRICAN_USER;
