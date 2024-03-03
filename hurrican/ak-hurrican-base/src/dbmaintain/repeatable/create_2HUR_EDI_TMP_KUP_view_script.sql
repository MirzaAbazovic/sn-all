--
-- VIEWS mit Zugriff auf die KuP-TAL Rechnungsdaten
--

CREATE OR REPLACE FORCE VIEW V3KUP_T_CARRIERBESTELLUNG AS
   SELECT b.LBZ,
          b.VTRNR,
          NVL (c.niederlassung, 'München') AS niederlassung,
          b.PRODAK_ORDER__NO,
          b.AUFTRAG_ID,
          b.BEREITSTELLUNG_AM,
          b.KUENDBESTAETIGUNG_CARRIER,
          b.ONKZ,
          b.ASB,
          A.EDT_ID,
          A.EDF_RECHNUNGSDATUM,
          A.EAL_NR_ELFE,
          A.EDT_VON,
          A.EDT_BIS,
          A.EDT_ANZ,
          A.EDT_BETRAG,
          A.EDT_LEITUNGSNR,
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
          A.EAL_NR,
          A.EAL_RECHNUNGSTEXT,
          A.EAL_TEXT,
          CAST (NULL AS VARCHAR2 (200)) AS bemerkungen
   FROM V1EDI_T_HVT_2_NL C,
        mnetcall.V2KUP_T_CARRIERBESTELLUNG@kupvis B,
        T_EDI_KUP_TMP A
   WHERE                                                     -- rownum = 1 and
        ADD_MONTHS (a.edt_von,
                    -2) > b.KUENDBESTAETIGUNG_CARRIER AND
         a.edt_betrag > 0 AND
         b.ONKZ ||
         '_' ||
         b.ASB = c.comp(+) AND
         a.LBZ_COMP = B.LBZ_COMP
   ORDER BY b.KUENDBESTAETIGUNG_CARRIER,
            b.LBZ;

GRANT SELECT ON V3KUP_T_CARRIERBESTELLUNG TO R_HURRICAN_USER;

------------------------------------------------------------------------------
-- fehlende KUP
------------------------------------------------------------------------------

CREATE OR REPLACE FORCE VIEW V4KUP_T_CARRIERBESTELLUNG_M AS
   SELECT c.niederlassung,
          A.EDT_ID,
          A.EDF_RECHNUNGSDATUM,
          A.EAL_NR_ELFE,
          A.EDT_VON,
          A.EDT_BIS,
          A.EDT_ANZ,
          A.EDT_BETRAG,
          A.EDT_LEITUNGSNR,
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
          A.EAL_NR,
          A.EAL_RECHNUNGSTEXT,
          A.EAL_TEXT,
          CAST (NULL AS VARCHAR2 (200)) AS bemerkungen
   FROM V1EDI_T_ONKZ_2_NL C,
        mnetcall.V2KUP_T_CARRIERBESTELLUNG@kupvis B,
        T_EDI_KUP_TMP A
   WHERE c.niederlassung = 'München' AND
         a.LBZ_ONKZ = c.onkzlen5 AND
         a.LBZ_COMP = B.LBZ_COMP(+) AND
         b.LBZ_COMP IS NULL
   ORDER BY b.KUENDBESTAETIGUNG_CARRIER,
            b.LBZ;

GRANT SELECT ON V4KUP_T_CARRIERBESTELLUNG_M TO R_HURRICAN_USER;

------------------------------------------------------------------------------
-- alle Nürnberg naja
------------------------------------------------------------------------------

CREATE OR REPLACE FORCE VIEW V4KUP_T_CARRIERBESTELLUNG_N AS
SELECT c.niederlassung,
       A.EDT_ID,
       A.EDF_RECHNUNGSDATUM,
       A.EAL_NR_ELFE,
       A.EDT_VON,
       A.EDT_BIS,
       A.EDT_ANZ,
       A.EDT_BETRAG,
       A.EDT_LEITUNGSNR,
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
       A.EAL_NR,
       A.EAL_RECHNUNGSTEXT,
       A.EAL_TEXT
FROM
     V1EDI_T_ONKZ_2_NL C,
     mnetcall.V2KUP_T_CARRIERBESTELLUNG@kupvis B,
     T_EDI_KUP_TMP A
WHERE
      a.LBZ_ONKZ = c.onkzlen5(+) AND
      c.onkzlen5 IS NULL AND
      a.LBZ_COMP = B.LBZ_COMP(+) AND
      b.LBZ_COMP IS NULL
ORDER BY
      b.KUENDBESTAETIGUNG_CARRIER,
      b.LBZ;

GRANT SELECT ON V4KUP_T_CARRIERBESTELLUNG_N TO R_HURRICAN_USER;

------------------------------------------------------------------
-- nur in KuP aktiv, aber fehlt auf der Rechnung
------------------------------------------------------------------

CREATE OR REPLACE FORCE VIEW V4KUP_T_CARRIERBESTELL_AKT_M AS
   SELECT c.niederlassung,
          A.CUST_NO,
          A.VALID_FROM,
          A.VALID_TO,
          A.OE__NO,
          A.LBZ_COMP,
          A.LBZ,
          A.VTRNR,
          A.PRODAK_ORDER__NO,
          A.AUFTRAG_ID,
          A.AUF_NR,
          A.ONKZ,
          A.ASB,
          CAST (NULL AS VARCHAR2 (200)) AS bemerkungen
   FROM V1EDI_T_HVT_2_NL C,
        T_EDI_KUP_TMP B,
        mnetcall.V2KUP_T_CARRIERBESTELLUNG_AKT@kupvis A
   WHERE a.ONKZ ||
         '_' ||
         a.ASB = c.comp(+) AND
         a.LBZ_COMP = B.LBZ_COMP(+) AND
         b.LBZ_COMP IS NULL
   ORDER BY A.VALID_FROM,
            a.cust_no,
            a.LBZ;

GRANT SELECT ON V4KUP_T_CARRIERBESTELLUNG_N TO R_HURRICAN_USER;
