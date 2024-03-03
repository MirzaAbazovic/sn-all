-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung der Endstellen.
--
-- Verwendete Tabellen:
--   + T_AUFTRAG_DATEN
--   + T_AUFTRAG_TECHNIK
--   + T_ENDSTELLE
--   + T_HVT_STANDORT
--   + T_HVT_GRUPPE
--   + T_ANSCHLUSSART
--   + T_ES_LTG_DATEN
--   + T_SCHNITTSTELLE
--   + T_LEITUNGSNUMMER (subselect)
--
-- Besonderheiten:
--   + Auftragsstatus "10000" (Konsolidiert) ausgeschlossen
--

-- Zus�tzliche Funktion zur Erstellung einer strichpunkt-separierten Liste der Leitungsnummern
CREATE OR REPLACE FUNCTION collect_leitungsnummern(aid IN varchar2)
  RETURN varchar2
AS
    result  varchar2(10000) := '';
    firstLn boolean         := true;
BEGIN
    FOR lnr IN (SELECT ln.LEITUNGSNUMMER FROM T_LEITUNGSNUMMER ln WHERE ln.AUFTRAG_ID = aid) LOOP
        IF NOT firstLn THEN
            result := result || CHR(59);
        END IF;

        result  := result || lnr.LEITUNGSNUMMER;
        firstLn := false;
    END LOOP;
   RETURN result;
END;
/

CREATE OR REPLACE FORCE VIEW V_HURRICAN_ACCESSPOINT
(
   TECH_ORDER_ID,
   ACCESSPOINT_ID,
   AP_TYPE,
   AP_GEO_ID,
   AP_LTG_KUNDE,
   AP_LTG_CARRIER,
   CONNECTIONTYPE,
   TECH_INTERFACE,
   AP_COMMENT,
   AP_ADDRESS_ID,
   RANGIER_ID,
   RANGIER_ID_ADD,
   TECH_LOCATION_ID,
   TECH_LOCATION_ASB,
   TECH_LOCATION_ONKZ,
   TECH_LOCATION_NAME,
   TECH_LOCATION_STREET,
   TECH_LOCATION_HOUSENUM,
   TECH_LOCATION_CITY,
   TECH_LOCATION_SWITCH
)
AS
   SELECT ad.AUFTRAG_ID AS TECH_ORDER_ID,
          e.ID AS ACCESSPOINT_ID,
          e.ES_TYP AS AP_TYPE,
          e.GEO_ID AS AP_GEO_ID,
          ad.LBZ_KUNDE AS AP_LTG_KUNDE,
          collect_leitungsnummern(ad.AUFTRAG_ID) AS AP_LTG_CARRIER,
          ans.ANSCHLUSSART AS CONNECTIONTYPE,
          s.SCHNITTSTELLE AS TECH_INTERFACE,
          e.BEMSTAWA AS AP_COMMENT,
          e.ADDRESS_ID AS AP_ADDRESS_ID,
          e.RANGIER_ID AS RANGIER_ID,
          e.RANGIER_ID_ADDITIONAL AS RANGIER_ID_ADD,
          e.HVT_ID_STANDORT AS TECH_LOCATION_ID,
          to_number(NVL(substr(hs.ASB, -3),ASB)) as TECH_LOCATION_ASB,
          hg.ONKZ AS TECH_LOCATION_ONKZ,
          hg.ORTSTEIL AS TECH_LOCATION_NAME,
          hg.STRASSE AS TECH_LOCATION_STREET,
          hg.HAUS_NR AS TECH_LOCATION_HOUSENUM,
          hg.ORT AS TECH_LOCATION_CITY,
          (
           CASE WHEN atech.HW_SWITCH IS NOT NULL
             THEN
               (SELECT name FROM T_HW_SWITCH WHERE ID = atech.HW_SWITCH)
           ELSE
             CASE WHEN eq.SWITCH IS NOT NULL
               THEN
                 (SELECT name FROM T_HW_SWITCH WHERE ID = eq.SWITCH)
             ELSE
               sw.NAME
             END
           END
          ) AS TECH_LOCATION_SWITCH
   FROM T_AUFTRAG_DATEN ad
        INNER JOIN T_AUFTRAG_TECHNIK atech ON ad.AUFTRAG_ID = atech.AUFTRAG_ID
        INNER JOIN T_ENDSTELLE e ON atech.AT_2_ES_ID = e.ES_GRUPPE
        LEFT JOIN T_RANGIERUNG rang ON rang.RANGIER_ID = e.RANGIER_ID
        LEFT JOIN T_EQUIPMENT eq ON eq.EQ_ID = rang.EQ_OUT_ID
        LEFT JOIN T_ANSCHLUSSART ans ON e.ANSCHLUSSART = ans.ID
        LEFT JOIN T_HVT_STANDORT hs ON e.HVT_ID_STANDORT = hs.HVT_ID_STANDORT
        LEFT JOIN T_HVT_GRUPPE hg ON hs.HVT_GRUPPE_ID = hg.HVT_GRUPPE_ID
        LEFT JOIN T_HW_SWITCH sw on (sw.ID = hg.SWITCH)
        LEFT JOIN T_ES_LTG_DATEN eltg ON e.ID = eltg.ES_ID
        LEFT JOIN T_SCHNITTSTELLE s ON eltg.SCHNITTSTELLE_ID = s.ID
   WHERE ad.GUELTIG_BIS > SYSDATE AND
         atech.GUELTIG_BIS > SYSDATE AND
         ad.STATUS_ID < 10000 AND
         (eltg.GUELTIG_BIS IS NULL OR
          eltg.GUELTIG_BIS > SYSDATE);

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_ACCESSPOINT FOR V_HURRICAN_ACCESSPOINT;

GRANT SELECT ON V_HURRICAN_ACCESSPOINT TO R_HURRICAN_BSI_VIEWS;
