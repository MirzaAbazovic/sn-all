-- Abfrage des ASB pro DSLAM / EWSD / MDU
-- (wird aus dem Service-Portal aufgerufen)

CREATE OR REPLACE FORCE VIEW V_OLT_U_PORTS
(
    GERAETEBEZ,
    PORT,
    ANZAHL
)
AS
  SELECT
    RACK.GERAETEBEZ,
    MDU.OLT_FRAME || '-' || MDU.OLT_SHELF || '-' || MDU.OLT_GPON_PORT AS PORT,
    count(*)                                                          AS ANZAHL
  FROM T_HW_RACK RACK
    INNER JOIN T_HW_RACK_MDU MDU ON (RACK.ID = MDU.OLT_RACK_ID)
  WHERE RACK_TYP IN ('OLT', 'DSLAM')
  GROUP BY RACK.GERAETEBEZ, MDU.OLT_FRAME, MDU.OLT_SHELF, MDU.OLT_GPON_PORT

  UNION

  SELECT
    RACK.GERAETEBEZ,
    EQUIPMENT.HW_EQN AS PORT,
    NULL             AS ANZAHL
  FROM T_HW_RACK RACK
    INNER JOIN T_HW_BAUGRUPPE BAUGRUPPE
      ON (RACK.ID = BAUGRUPPE.RACK_ID)
    INNER JOIN T_EQUIPMENT EQUIPMENT
      ON (Baugruppe.id = EQUIPMENT.HW_BAUGRUPPEN_ID)
  WHERE RACK_TYP = 'OLT'

  UNION

  SELECT
    RACK.GERAETEBEZ,
    ONT.OLT_FRAME || '-' || ONT.OLT_SHELF || '-' || ONT.OLT_GPON_PORT AS PORT,
    count(*)                                                          AS ANZAHL
  FROM T_HW_RACK RACK
    INNER JOIN T_HW_RACK_ONT ONT ON (RACK.ID = ONT.OLT_RACK_ID)
  WHERE RACK_TYP IN ('OLT', 'DSLAM')
  GROUP BY RACK.GERAETEBEZ, ONT.OLT_FRAME, ONT.OLT_SHELF, ONT.OLT_GPON_PORT

  UNION

  SELECT
    RACK.GERAETEBEZ,
    DPO.OLT_FRAME || '-' || DPO.OLT_SHELF || '-' || DPO.OLT_GPON_PORT AS PORT,
    count(*)                                                          AS ANZAHL
  FROM T_HW_RACK RACK
    INNER JOIN T_HW_RACK_DPO DPO ON (RACK.ID = DPO.OLT_RACK_ID)
  WHERE RACK_TYP IN ('OLT', 'DSLAM')
  GROUP BY RACK.GERAETEBEZ, DPO.OLT_FRAME, DPO.OLT_SHELF, DPO.OLT_GPON_PORT

  ORDER BY 1, 2;

GRANT SELECT ON V_OLT_U_PORTS TO HURRICAN_TECH_MUC;
