-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung von Stammdaten der technischen
-- Auftraege aus Hurrican.
--
-- Verwendete Tabellen:
--   + T_AUFTRAG
--   + T_AUFTRAG_DATEN
--   + T_AUFTRAG_TECHNIK
--   + T_TDN
--   + T_AUFTRAG_STATUS
--   + T_PRODUKT
--   + T_BA_VERL_ANLASS
--   + T_HW_SWITCH
--
-- Besonderheiten:
--   + Auftragsstatus "10000" (Konsolidiert) ausgeschlossen
--   + Texte fuer Tel-Buch/Inverssuche sind hard-coded!
--
-- Hinweise:
--   - NULL as CONNECTIONTYPE kann gel�scht werden, sobald OR zur L�schung von "Realisiert �ber" in BSI erledigt
--   - NULL as CONNECTION_TYPE_ID kann gel�scht werden, sobald OR zur L�schung von "Realisiert �ber" in BSI erledigt


CREATE OR REPLACE VIEW V_HURRICAN_TECH_ORDER_BASE
 AS
  SELECT
    a.KUNDE__NO as CUST_NO,
    ad.PRODAK_ORDER__NO as TAIFUN_ORDER__NO,
    a.ID as TECH_ORDER_ID,
    ad.BEARBEITER as BEARBEITER,
    ad.VORGABE_SCV as PLANNED_START,
    ad.INBETRIEBNAHME as REAL_DATE,
    ad.KUENDIGUNG as CANCEL_DATE,
    ast.STATUS_TEXT as TECH_STATUS,
    ad.STATUS_ID as TECH_STATUS_ID,
    p.ANSCHLUSSART as TECH_PRODUCT,
    p.PROD_ID as TECH_PRODUCT_ID,
    NULL as CONNECTIONTYPE,
    NULL as CONNECTION_TYPE_ID,
    bva.NAME as REAL_TYPE,
    bva.ID as REAL_TYPE_ID,
    tdn.TDN as TDN,
    tdn.ID as TDN_ID,
    CASE WHEN ad.TELBUCH=1 THEN 'erledigt'
      ELSE CASE WHEN ad.TELBUCH=2 THEN 'nicht gewuenscht'
        ELSE ''
        END
      END PHONEBOOK,
    CASE WHEN ad.INVERSSUCHE=1 THEN 'erledigt'
      ELSE CASE WHEN ad.INVERSSUCHE=2 THEN 'nicht gewuenscht'
        ELSE CASE WHEN ad.INVERSSUCHE=3 THEN 'widersprochen'
          ELSE ''
          END
        END
      END INVERSSUCHE,
    CASE WHEN ad.BUENDEL_NR IS NOT NULL and ad.BUENDEL_NR_HERKUNFT is NOT NULL THEN BUENDEL_NR_HERKUNFT || '_' || BUENDEL_NR
      ELSE ''
      END BUNDLE_NO,
    hwswch.name as HW_SWITCH_NAME,
    ad.LBZ_KUNDE as LBZ_KUNDE
  FROM T_AUFTRAG a
    INNER JOIN T_AUFTRAG_DATEN ad on a.ID=ad.AUFTRAG_ID
    INNER JOIN T_AUFTRAG_TECHNIK atech on a.ID=atech.AUFTRAG_ID
    LEFT JOIN T_TDN tdn on atech.TDN_ID=tdn.ID
    INNER JOIN T_AUFTRAG_STATUS ast on ad.STATUS_ID=ast.ID
    INNER JOIN T_PRODUKT p on ad.PROD_ID=p.PROD_ID
    LEFT JOIN T_BA_VERL_ANLASS bva on atech.AUFTRAGSART=bva.ID
    LEFT JOIN T_HW_SWITCH hwswch on hwswch.ID = atech.HW_SWITCH
  WHERE
    ad.GUELTIG_BIS > SYSDATE
    and ad.STATUS_ID < 10000
    and atech.GUELTIG_BIS > SYSDATE;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_TECH_ORDER_BASE FOR V_HURRICAN_TECH_ORDER_BASE;

GRANT SELECT ON V_HURRICAN_TECH_ORDER_BASE TO R_HURRICAN_BSI_VIEWS;
