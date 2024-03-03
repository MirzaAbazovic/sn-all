Insert into T_BA_VERL_ANLASS
   (ID, NAME, BA_VERL_GRUPPE, IS_CONFIGURABLE, IS_AUFTRAGSART,
    POSITION_NO, AKT, CPS_SO_TYPE, VERSION, FFM_TYP)
 Values
   (45, 'automatischer Downgrade', 1, '1', '0', NULL, '1', 14000, 0, NULL);


-- BA-Konfiguration fuer Produkte 512/513 --> mit automatischer Verteilung an M-Queue
Insert into T_BA_VERL_CONFIG
   (ID, PROD_ID, ANLASS, ABT_CONFIG_ID, GUELTIG_VON,
    GUELTIG_BIS, DATEW, USERW,
    VERSION, AUTO_VERTEILEN, CPS_NECESSARY)
 Values
   (S_T_BA_VERL_CONFIG_0.nextVal, 512, 45, 17, sysdate,
   TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   sysdate,
   'DBMaintain',
   0, '1', '1');

Insert into T_BA_VERL_CONFIG
   (ID, PROD_ID, ANLASS, ABT_CONFIG_ID, GUELTIG_VON,
    GUELTIG_BIS, DATEW, USERW,
    VERSION, AUTO_VERTEILEN, CPS_NECESSARY)
 Values
   (S_T_BA_VERL_CONFIG_0.nextVal, 513, 45, 17, sysdate,
   TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   sysdate,
   'DBMaintain',
   0, '1', '1');
