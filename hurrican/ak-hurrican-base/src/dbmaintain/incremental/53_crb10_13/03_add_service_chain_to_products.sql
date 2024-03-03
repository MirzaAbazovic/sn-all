-- set new service chain to products 540 and 541

-- update product 540
UPDATE T_PRODUKT
    SET VERLAUF_CHAIN_ID = 125, MAX_DN_COUNT = 40
    WHERE PROD_ID = 540;

-- update product 541
UPDATE T_PRODUKT
    SET VERLAUF_CHAIN_ID = 125, DN_TYP = 60, MAX_DN_COUNT = 40, MIN_DN_COUNT = 1
    WHERE PROD_ID = 541;