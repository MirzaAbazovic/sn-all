-- ANF-722 wird nicht umgesetzt
-- TV Signallieferung MV für FFM konfiguration wieder entfernen
delete from T_FFM_PRODUCT_MAPPING where PROD_ID = 522 and
(FFM_ACTIVITY_TYPE = 'RTL_sonstige' or FFM_ACTIVITY_TYPE = 'RTL_Kuendigung' or FFM_ACTIVITY_TYPE = 'RTL_Entstoerung');
