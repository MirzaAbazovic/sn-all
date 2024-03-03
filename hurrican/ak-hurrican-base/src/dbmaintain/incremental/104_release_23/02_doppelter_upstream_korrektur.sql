-- 300 Mbit gibt es für Surf&Fon nicht
DELETE FROM T_PROD_2_TECH_LEISTUNG where PROD_ID = 512 and ((TECH_LS_ID = 66 and TECH_LS_DEPENDENCY = 544) OR
              (TECH_LS_ID = 53 and TECH_LS_DEPENDENCY = 66) OR (TECH_LS_ID = 53 and TECH_LS_DEPENDENCY = 544));

-- Korrektur Namen für 100/40 VOIP_IPv6
UPDATE T_TECH_LEISTUNG set NAME = '100/40_VOIP_IPv6 testen' where ID = '542' and EXTERN_LEISTUNG__NO = 10227;
