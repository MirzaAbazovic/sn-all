-- Optionale CV-Lans fuer FTTX-Telefon �ber Fritz!Box 7360, abh�ngig von der Leistung "CPE"

INSERT INTO T_PROD_2_CVLAN (ID,VERSION,PROD_ID,CVLAN_TYP,IS_MANDATORY,TECH_LOCATION_TYPE_REF_ID,HVT_TECHNIK_ID) VALUES (S_T_PROD_2_CVLAN_0.nextVal,0,511,'HSI','0',null,null);
INSERT INTO T_PROD_2_CVLAN (ID,VERSION,PROD_ID,CVLAN_TYP,IS_MANDATORY,TECH_LOCATION_TYPE_REF_ID,HVT_TECHNIK_ID) VALUES (S_T_PROD_2_CVLAN_0.nextVal,0,511,'IAD','0',null,null);

INSERT INTO T_TECH_LEISTUNG_2_CVLAN (ID,VERSION,TECH_LS_ID,CVLAN_TYP) VALUES (S_T_TECH_LEISTUNG_2_CVLAN_0.nextVal,0,560,'HSI');
INSERT INTO T_TECH_LEISTUNG_2_CVLAN (ID,VERSION,TECH_LS_ID,CVLAN_TYP) VALUES (S_T_TECH_LEISTUNG_2_CVLAN_0.nextVal,0,560,'IAD');