-- SQL wurde in Produktion bereits ausgefuehrt, dieses Skript dient der Dokumentation
-- Aenderung wurde im Rahmen von HUR-23366 durchgefuehrt, nach Absprache mit T. Botzenhardt
Insert into T_FFM_PRODUCT_MAPPING
   (ID, VERSION, PROD_ID, FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY, BA_FFM_TYP)
   select S_T_FFM_PRODUCT_MAPPING_0.nextval, 0, 580, 'RTL_Entstoerung', 90,
    'TECHNICAL_PARAMS_INCLUDED_WITH_TIMESLOT', 'ENTSTOERUNG'
    from dual where not exists (select * from T_FFM_PRODUCT_MAPPING where PROD_ID = 580 and FFM_ACTIVITY_TYPE = 'RTL_Entstoerung')
    ;

Insert into T_FFM_PRODUCT_MAPPING
   (ID, VERSION, PROD_ID, FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY, BA_FFM_TYP)
   select S_T_FFM_PRODUCT_MAPPING_0.nextval, 0, 580, 'RTL_Kuendigung', 30,
    'TECHNICAL_PARAMS_INCLUDED_WITH_TIMESLOT', 'KUENDIGUNG'
    from dual where not exists (select * from T_FFM_PRODUCT_MAPPING where PROD_ID = 580 and FFM_ACTIVITY_TYPE = 'RTL_Kuendigung')
    ;
