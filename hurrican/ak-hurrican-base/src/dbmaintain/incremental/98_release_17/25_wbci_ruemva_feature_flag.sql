alter table T_FEATURE modify NAME VARCHAR2(50);

Insert into T_FEATURE
   (ID, NAME, FLAG, VERSION)
 Values
   (S_T_FEATURE_0.nextVal, 'WBCI_ABGEBEND_RUEMVA_AUTO_PROCESSING', '0', 0);
