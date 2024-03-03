create or replace force VIEW MIGRATION_CC_HUAWEI as
select
  distinct
  gr.NIEDERLASSUNG_ID,
  ad.AUFTRAG_ID,
  ad.STATUS_ID as AUFTRAG_STATUS_ID,
  eq.EQ_ID as EQUIPMENT_ID,
  eq.HW_EQN,
  hwb.MOD_NUMBER,
  eq.HW_SCHNITTSTELLE,
  d.DSLAM_TYPE,
  v.REALISIERUNGSTERMIN
FROM T_AUFTRAG a
  INNER JOIN T_AUFTRAG_DATEN ad ON a.ID = ad.AUFTRAG_ID
  INNER JOIN T_AUFTRAG_TECHNIK at ON at.AUFTRAG_ID = a.ID
  INNER JOIN T_ENDSTELLE es ON at.AT_2_ES_ID = es.ES_GRUPPE
  INNER JOIN t_rangierung rang ON ES.RANGIER_ID = RANG.RANGIER_ID
  INNER JOIN t_equipment eq ON RANG.EQ_IN_ID = EQ.EQ_ID
  INNER JOIN t_hw_baugruppe hwb ON EQ.HW_BAUGRUPPEN_ID = HWB.ID
  INNER JOIN t_hw_rack hwr ON HWB.RACK_ID = HWR.ID
  INNER JOIN T_HW_RACK_DSLAM d ON hwr.ID = d.RACK_ID
  INNER JOIN T_HVT_STANDORT s ON hwr.HVT_ID_STANDORT = s.HVT_ID_STANDORT
  INNER JOIN T_HVT_GRUPPE gr ON gr.HVT_GRUPPE_ID = s.HVT_GRUPPE_ID
  inner JOIN T_VERLAUF v ON v.AUFTRAG_ID = ad.AUFTRAG_ID and v.AKT=1 and v.REALISIERUNGSTERMIN >= sysdate
WHERE
  es.ES_TYP = 'B'
  and ad.GUELTIG_von <= sysdate and ad.GUELTIG_bis > sysdate
  and at.GUELTIG_von <= sysdate and at.GUELTIG_bis > sysdate
  and eq.GUELTIG_von <= sysdate and eq.GUELTIG_BIS > sysdate
  and hwr.GUELTIG_von <= sysdate and hwr.GUELTIG_BIS > sysdate
  AND ad.STATUS_ID in (4000, 9105)
  AND hwr.HW_PRODUCER = 2
  AND d.DSLAM_TYPE is not NULL
UNION
select
  distinct
  gr.NIEDERLASSUNG_ID,
  ad.AUFTRAG_ID,
  ad.STATUS_ID as AUFTRAG_STATUS_ID,
  eq.EQ_ID as EQUIPMENT_ID,
  eq.HW_EQN,
  hwb.MOD_NUMBER,
  eq.HW_SCHNITTSTELLE,
  d.DSLAM_TYPE,
  null as REALISIERUNGSTERMIN
FROM T_AUFTRAG a
  INNER JOIN T_AUFTRAG_DATEN ad ON a.ID = ad.AUFTRAG_ID
  INNER JOIN T_AUFTRAG_TECHNIK at ON at.AUFTRAG_ID = a.ID
  INNER JOIN T_ENDSTELLE es ON at.AT_2_ES_ID = es.ES_GRUPPE
  INNER JOIN t_rangierung rang ON ES.RANGIER_ID = RANG.RANGIER_ID
  INNER JOIN t_equipment eq ON RANG.EQ_IN_ID = EQ.EQ_ID
  INNER JOIN t_hw_baugruppe hwb ON EQ.HW_BAUGRUPPEN_ID = HWB.ID
  INNER JOIN t_hw_rack hwr ON HWB.RACK_ID = HWR.ID
  INNER JOIN T_HW_RACK_DSLAM d ON hwr.ID = d.RACK_ID
  INNER JOIN T_HVT_STANDORT s ON hwr.HVT_ID_STANDORT = s.HVT_ID_STANDORT
  INNER JOIN T_HVT_GRUPPE gr ON gr.HVT_GRUPPE_ID = s.HVT_GRUPPE_ID
WHERE
  es.ES_TYP = 'B'
  and ad.GUELTIG_von <= sysdate and ad.GUELTIG_bis > sysdate
  and at.GUELTIG_von <= sysdate and at.GUELTIG_bis > sysdate
  and eq.GUELTIG_von <= sysdate and eq.GUELTIG_BIS > sysdate
  and hwr.GUELTIG_von <= sysdate and hwr.GUELTIG_BIS > sysdate
  AND ad.STATUS_ID in (6000, 6100, 6200, 9000, 9100)
  AND hwr.HW_PRODUCER = 2
  AND d.DSLAM_TYPE is not NULL
ORDER BY AUFTRAG_ID
;

grant select on MIGRATION_CC_HUAWEI to R_HURRICAN_USER;
