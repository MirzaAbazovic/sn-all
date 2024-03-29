-- Korrektur der FFM_PLANED_DURATION für RTL_NEU an HVT: von 45 auf 90 Minuten
UPDATE T_FFM_PRODUCT_MAPPING SET FFM_PLANNED_DURATION = 90
  WHERE STANDORT_TYP in (11000, 11001)
    AND lower(FFM_ACTIVITY_TYPE) LIKE 'rtl_neu%'
    AND FFM_PLANNED_DURATION = 45;


UPDATE T_FFM_PRODUCT_MAPPING SET FFM_PLANNED_DURATION = 90
  WHERE FFM_ACTIVITY_TYPE = 'RTL_Entstoerung';

delete from T_FFM_PRODUCT_MAPPING WHERE
  STANDORT_TYP=11001 and FFM_ACTIVITY_TYPE='RTL_NEU_IK';

Insert into T_FFM_PRODUCT_MAPPING
   (ID, VERSION, STANDORT_TYP, FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION,
    AGGREGATION_STRATEGY, BA_FFM_TYP)
 Values
   (S_T_FFM_PRODUCT_MAPPING_0.nextVal, 0, 11001, 'RTL_Entstoerung', 90,
    'TECHNICAL_PARAMS_INCLUDED_WITH_TIMESLOT', 'ENTSTOERUNG');
