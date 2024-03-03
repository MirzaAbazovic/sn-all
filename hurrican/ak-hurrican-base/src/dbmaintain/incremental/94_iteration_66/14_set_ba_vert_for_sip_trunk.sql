-- BA-Verteilung bei Neuschaltung an M-Queue für SIP-Trunk
INSERT INTO T_BA_VERL_CONFIG (ID, PROD_ID, ANLASS, ABT_CONFIG_ID, GUELTIG_VON, GUELTIG_BIS, DATEW, USERW, VERSION, AUTO_VERTEILEN, CPS_NECESSARY)
  VALUES (S_T_BA_VERL_CONFIG_0.nextVal, 580, 27, 17, to_date('10.01.2014', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), sysdate, 'dbscript', 0, '1', '1');

-- BA-Verteilung bei Kündigung an M-Queue für SIP-Trunk
INSERT INTO T_BA_VERL_CONFIG (ID, PROD_ID, ANLASS, ABT_CONFIG_ID, GUELTIG_VON, GUELTIG_BIS, DATEW, USERW, VERSION, AUTO_VERTEILEN, CPS_NECESSARY)
  VALUES (S_T_BA_VERL_CONFIG_0.nextVal, 580, 13, 17, to_date('10.01.2014', 'DD.MM.YYYY'), to_date('01.01.2200', 'DD.MM.YYYY'), sysdate, 'dbscript', 0, '1', '1');
