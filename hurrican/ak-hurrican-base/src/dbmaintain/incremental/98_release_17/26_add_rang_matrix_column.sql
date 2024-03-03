-- ANF-217.01 neuer HVT Standorttyp (FTTB_H)
-- nicht alle Physiktypen sollen in der Rangierungsmatrix beruecksichtigt werden, selbst wenn sie zu einem
-- Produkt konfiguriert sind (manuelle vs. automatischer Portzuordnung der Endstelle).
ALTER TABLE T_PRODUKT_2_PHYSIKTYP ADD (USE_IN_RANG_MATRIX CHAR(1));

UPDATE T_PRODUKT_2_PHYSIKTYP
SET USE_IN_RANG_MATRIX = '1';
UPDATE T_PRODUKT_2_PHYSIKTYP
SET USE_IN_RANG_MATRIX = '0'
WHERE PHYSIKTYP IN (SELECT ID
                    FROM T_PHYSIKTYP
                    WHERE NAME LIKE 'FTTH%');
