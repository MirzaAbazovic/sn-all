--
-- View zur Nachmigration fehlender Rufnummerleistungen
--

CREATE OR REPLACE FORCE VIEW MIG_V_ADSL_TELEFONIELEISTUNG AS
SELECT   AUFTRAG_ID_HURRICAN,
         AD.PROD_ID
  FROM               MIG_AUFTRAG_MAPPING m
                  JOIN
                     T_AUFTRAG_DATEN ad
                  ON M.AUFTRAG_ID_HURRICAN = AD.AUFTRAG_ID
               LEFT JOIN
                  T_PRODUKT p
               ON AD.PROD_ID = P.PROD_ID
            LEFT JOIN
               T_AUFTRAG_STATUS s
            ON AD.STATUS_ID = s.id
         LEFT JOIN
            T_AUFTRAG_2_Tech_LS a2tl
         ON (AD.AUFTRAG_ID = A2TL.AUFTRAG_ID
             AND a2tl.tech_ls_id IN (100, 101, 102))
 WHERE       INSERT_TIMESTAMP > TO_DATE ('01.01.2010', 'DD.MM.YYYY')
         AND P.MIN_DN_COUNT > 0
         AND s.id >= 1200
         AND s.id <= 9700
         AND s.id <> 3400
         AND s.id <> 1150
         AND a2tl.tech_ls_id IS NULL;

