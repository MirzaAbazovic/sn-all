-- Das Produkt ADSL-QSC unterstuetzt die Schnittstelle SDSL nicht
DELETE FROM t_produkt_2_schnittstelle
WHERE prod_id = 447 AND schnittstelle_id = 19;

-- ADSL-D 16/1 zieht ADSL-D und nicht ADSL-T an
UPDATE t_prod_2_tech_leistung
SET tech_ls_id = 456
WHERE PROD_ID = 447
      AND TECH_LS_ID = 455
      AND TECH_LS_DEPENDENCY = 453;
