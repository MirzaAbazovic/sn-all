-- 300 Mbit gibt es für Surf&Fon immer noch nicht
DELETE FROM T_PROD_2_TECH_LEISTUNG where PROD_ID = 512 and ((TECH_LS_ID = 57 and TECH_LS_DEPENDENCY = 544)
 OR (TECH_LS_ID = 544 and TECH_LS_DEPENDENCY is null));
