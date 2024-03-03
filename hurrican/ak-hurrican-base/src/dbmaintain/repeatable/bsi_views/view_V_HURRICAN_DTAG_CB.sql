CREATE OR REPLACE VIEW V_HURRICAN_DTAG_CB AS
  SELECT
    DISTINCT
    ad.PRODAK_ORDER__NO                        AS TAIFUN_ORDER__NO,
    kunde.KUNDE_NR                             AS DTAG_KUNDE_NR,
    kunde.WITA_LEISTUNGS_NR                    AS DTAG_LEISTUNGS_NR,
    (CASE kunde.KUNDE_NR
     WHEN besteller_muc.KUNDE_NR THEN NULL
     ELSE besteller_muc.KUNDE_NR END)          AS BESTELLER_KUNDE_NR,
    (CASE kunde.KUNDE_NR
     WHEN besteller_muc.KUNDE_NR THEN NULL
     ELSE besteller_muc.WITA_LEISTUNGS_NR END) AS BESTELLER_LEISTUNGS_NR,
    (CASE (substr(cb.lbz, 1, 3))
     WHEN '96U' THEN 'TAL; CuDA 2 Draht (HVt)'
     WHEN '96Y' THEN 'TAL; CuDA 2 Draht mit ZwR'
     WHEN '96Z' THEN 'TAL; CuDA 2 Draht mit ZwR'
     WHEN '96W' THEN 'TAL; CuDA 2 Draht hbr (HVt)'
     WHEN '96X' THEN 'TAL; CuDA 4 Draht hbr (HVt)'
     WHEN '9KW' THEN 'TAL; CuDA 2 Draht hbr (KVz)'
     WHEN '9KX' THEN 'TAL; CuDA 4 Draht hbr (KVz)'
     WHEN '95W' THEN 'TAL; Glasfaser 2 Fasern'
     WHEN '96R' THEN 'TAL; CCA-Analog'
     WHEN '96Q' THEN 'TAL; CCA-Basic ohne ZwR'
     WHEN '96T' THEN 'TAL; CCA-Primary'
     WHEN '95R' THEN 'TAL; Analoge TelAsl bei OPAL'
     WHEN '95S' THEN 'TAL; BaAsl bei OPAL'
     WHEN '95T' THEN 'TAL; Analoge TelAsl bei ISIS-outdoor'
     WHEN '95U' THEN 'TAL; BaAsl bei ISIS-outdoor'
     WHEN '96D' THEN 'TAL; PMxAsl bei ISIS-outdoor'
     WHEN '96C' THEN 'TAL; CuDA 2 Draht hbr (AAL)'
     WHEN '96S' THEN 'TAL; CuDA 4 Draht hbr (AAL)'
     WHEN '9KU' THEN 'TAL; CuDA 2 Draht (KVz)'
     WHEN '95V' THEN 'TAL; Glasfaser 1 Faser'
     ELSE 'unbekannt' END)                     AS PROD_BEZEICHNER,
    cb.VTRNR                                   AS VERTRAGSNUMMER,
    (CASE (adress.NAME)
     WHEN NULL THEN es.name
     ELSE adress.NAME END)                     AS ENDSTELLE_NAME,
    adress.strasse_add                         AS LAGE_TAE,
    cb.LBZ                                     AS LBZ,
    eq.HW_EQN                                  AS HW_EQN
  FROM T_CARRIERBESTELLUNG cb
    JOIN T_ENDSTELLE es ON (cb.CB_2_ES_ID = es.CB_2_ES_ID)
    LEFT JOIN T_RANGIERUNG r ON es.RANGIER_ID = r.RANGIER_ID
    LEFT JOIN T_EQUIPMENT eq ON r.EQ_IN_ID = eq.EQ_ID
    JOIN T_AUFTRAG_TECHNIK aut ON (
      aut.AT_2_ES_ID = ES.ES_GRUPPE
      AND aut.GUELTIG_BIS = TO_DATE('01.01.2200', 'dd.mm.yyyy')
      )
    JOIN T_AUFTRAG_DATEN ad ON (
      aut.AUFTRAG_ID = AD.AUFTRAG_ID
      AND AD.GUELTIG_BIS = TO_DATE('01.01.2200', 'dd.mm.yyyy')
      )
    LEFT JOIN T_ADDRESS adress ON (es.ADDRESS_ID = adress.ID)
    JOIN T_HVT_STANDORT hvt ON (es.hvt_id_standort = hvt.hvt_id_standort)
    JOIN T_CARRIER_KENNUNG kunde ON (hvt.CARRIER_KENNUNG_ID = kunde.ID)
    JOIN T_CARRIER_KENNUNG besteller_muc ON (besteller_muc.ID = 4)
  WHERE cb.CARRIER_ID = 12
;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_DTAG_CB FOR V_HURRICAN_DTAG_CB;

GRANT SELECT ON V_HURRICAN_DTAG_CB TO R_HURRICAN_BSI_VIEWS;
