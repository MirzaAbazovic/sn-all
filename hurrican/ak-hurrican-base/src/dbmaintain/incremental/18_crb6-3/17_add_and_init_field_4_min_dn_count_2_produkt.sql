
ALTER TABLE T_PRODUKT ADD MIN_DN_COUNT NUMBER(10);
COMMENT ON COLUMN T_PRODUKT.MIN_DN_COUNT IS 'minimale Anzahl an Rufnummern f�r dieses Produkt';

UPDATE T_PRODUKT SET MIN_DN_COUNT = 1 WHERE MAX_DN_COUNT = 1;
UPDATE T_PRODUKT SET MIN_DN_COUNT = 3 WHERE MAX_DN_COUNT = 10;
UPDATE T_PRODUKT SET MIN_DN_COUNT = 1 WHERE MAX_DN_COUNT = 1000;

-- special treatment for "Maxi Glasfaser-DSL Doppel-Flat"
UPDATE T_PRODUKT SET MIN_DN_COUNT = 1 WHERE PROD_ID = 513;

-- special treatment for "AK-SDSL S0" where BRAUCHT_DN = 1 but no MAX_DN_COUNT
UPDATE T_PRODUKT SET MIN_DN_COUNT = 1 WHERE PROD_ID = 12;

UPDATE T_PRODUKT SET MIN_DN_COUNT = 0 WHERE MIN_DN_COUNT IS NULL;

ALTER TABLE T_PRODUKT DROP COLUMN BRAUCHT_DN;

ALTER TABLE T_PRODUKT MODIFY MIN_DN_COUNT NOT NULL;
