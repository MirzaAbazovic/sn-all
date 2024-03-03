-- Default CVLANs fuer ProdId 600 (FTTB Wholesale Produkt)
insert into T_PROD_2_CVLAN (ID, VERSION, PROD_ID, CVLAN_TYP, IS_MANDATORY)
  values (S_T_PROD_2_CVLAN_0.nextVal, 0, 600, 'HSI', '1');
insert into T_PROD_2_CVLAN (ID, VERSION, PROD_ID, CVLAN_TYP, IS_MANDATORY)
  values (S_T_PROD_2_CVLAN_0.nextVal, 0, 600, 'VOIP', '1');
-- Optionale CVLANs fuer ProdId 600 (FTTB Wholesale Produkt)
insert into T_PROD_2_CVLAN (ID, VERSION, PROD_ID, CVLAN_TYP, IS_MANDATORY)
  values (S_T_PROD_2_CVLAN_0.nextVal, 0, 600, 'MC', '0');
insert into T_PROD_2_CVLAN (ID, VERSION, PROD_ID, CVLAN_TYP, IS_MANDATORY)
  values (S_T_PROD_2_CVLAN_0.nextVal, 0, 600, 'VOD', '0');
  
-- CVLAN Typen fuer techn. Leistung 'TP'
insert into T_TECH_LEISTUNG_2_CVLAN (ID, VERSION, TECH_LS_ID, CVLAN_TYP)
  values (S_T_TECH_LEISTUNG_2_CVLAN_0.nextVal, 0, 425, 'MC');
insert into T_TECH_LEISTUNG_2_CVLAN (ID, VERSION, TECH_LS_ID, CVLAN_TYP)
  values (S_T_TECH_LEISTUNG_2_CVLAN_0.nextVal, 0, 425, 'VOD');
  