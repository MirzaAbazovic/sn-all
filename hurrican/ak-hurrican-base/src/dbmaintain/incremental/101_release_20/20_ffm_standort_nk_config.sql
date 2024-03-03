-- SQL wurde in Produktion bereits ausgefuehrt, dieses Skript dient der Dokumentation
-- Aenderung wurde im Rahmen von HUR-23272 durchgefuehrt, nach Absprache mit T. Botzenhardt
Insert into T_FFM_PRODUCT_MAPPING
   (ID, VERSION, STANDORT_TYP, FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY, BA_FFM_TYP)
   select S_T_FFM_PRODUCT_MAPPING_0.nextval, 0, 11004, 'RTL_Entstoerung', 90,
    'TECHNICAL_PARAMS_INCLUDED_WITH_TIMESLOT', 'ENTSTOERUNG'
    from dual where not exists (select * from T_FFM_PRODUCT_MAPPING where STANDORT_TYP = 11004 and FFM_ACTIVITY_TYPE = 'RTL_Entstoerung')
    ;

Insert into T_FFM_PRODUCT_MAPPING
   (ID, VERSION, STANDORT_TYP, FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY, BA_FFM_TYP)
   select S_T_FFM_PRODUCT_MAPPING_0.nextval, 0, 11004, 'RTL_Neu_MK_HVT', 45,
    'TECHNICAL_PARAMS_INCLUDED_WITH_TIMESLOT', 'NEU'
    from dual where not exists (select * from T_FFM_PRODUCT_MAPPING where STANDORT_TYP = 11004 and FFM_ACTIVITY_TYPE = 'RTL_Neu_MK_HVT')
    ;

Insert into T_FFM_PRODUCT_MAPPING
   (ID, VERSION, STANDORT_TYP, FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY, BA_FFM_TYP)
   select S_T_FFM_PRODUCT_MAPPING_0.nextval, 0, 11004, 'RTL_Kuendigung', 30,
    'TECHNICAL_PARAMS_INCLUDED_WITH_TIMESLOT', 'KUENDIGUNG'
    from dual where not exists (select * from T_FFM_PRODUCT_MAPPING where STANDORT_TYP = 11004 and FFM_ACTIVITY_TYPE = 'RTL_Kuendigung')
    ;
