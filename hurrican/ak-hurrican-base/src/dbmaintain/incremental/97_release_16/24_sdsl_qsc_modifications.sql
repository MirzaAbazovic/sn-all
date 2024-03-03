-- ADSL-QSC gueltig ab 1ten August
UPDATE T_PRODUKT
SET GUELTIG_VON = TO_DATE('08/01/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS')
WHERE PROD_ID = 447;

-- Das Produkt SDSL-QSC unterstuetzt die Schnittstelle ADSL nicht
DELETE FROM t_produkt_2_schnittstelle
WHERE prod_id = 448 AND schnittstelle_id = 8;

-- Das Produkt SDSL-QSC unterstuetzt die Schnittstelle ADSL2+ nicht
DELETE FROM t_produkt_2_schnittstelle
WHERE prod_id = 448 AND schnittstelle_id = 32;
