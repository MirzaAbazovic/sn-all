-- ANF-217.01 neuer HVT Standorttyp (FTTB_H)
-- Derzeit betroffene Produkte:
-- 500, 501, 502, 503, 511, 512, 513, 520, 521, 540, 541, 542, 570, 600
CREATE OR REPLACE PROCEDURE inc_priorities_by_one(prod_id_param IN NUMBER, prio_param IN NUMBER)
AS
  this_id T_PRODUKT_2_TECH_LOCATION_TYPE.ID%TYPE;
  BEGIN
    SELECT
        p2tlt.id
      INTO this_id
      FROM T_PRODUKT_2_TECH_LOCATION_TYPE p2tlt
      WHERE p2tlt.prod_id = prod_id_param AND p2tlt.priority = prio_param;
    IF this_id IS NOT NULL THEN
      inc_priorities_by_one(prod_id_param, prio_param + 1);
      UPDATE T_PRODUKT_2_TECH_LOCATION_TYPE
        SET priority = prio_param + 1
        WHERE id = this_id;
    END IF;
    EXCEPTION
      WHEN OTHERS THEN
      this_id := NULL;
  END;
/

CREATE OR REPLACE PROCEDURE insert_fttb_h_with_prio(prod_id IN NUMBER, priority IN NUMBER)
IS
  BEGIN
    inc_priorities_by_one(prod_id, priority);
    INSERT INTO T_PRODUKT_2_TECH_LOCATION_TYPE
      (ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW, VERSION)
    VALUES
      (S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextval, prod_id, 11017, priority, 'Reiner Jürgen', 0);
  END;
/

CREATE OR REPLACE PROCEDURE updateProd2TechLocationType
IS
  BEGIN
    FOR rec IN (SELECT
                  p2tlt.prod_id,
                  (SELECT
                  max(p2tlt2.priority)
                   FROM T_PRODUKT_2_TECH_LOCATION_TYPE p2tlt2
                   WHERE p2tlt2.PROD_ID = p2tlt.prod_id AND p2tlt2.tech_location_type_ref_id IN (11002, 11011)) AS PRIO
                FROM T_PRODUKT_2_TECH_LOCATION_TYPE p2tlt
                WHERE p2tlt.tech_location_type_ref_id IN (11002, 11011)
                GROUP BY p2tlt.prod_id
                ORDER BY p2tlt.prod_id)
    LOOP
      insert_fttb_h_with_prio(rec.PROD_ID, rec.PRIO + 1);
    END LOOP;
  END;
/

CALL updateProd2TechLocationType();
DROP PROCEDURE inc_priorities_by_one;
DROP PROCEDURE insert_fttb_h_with_prio;
DROP PROCEDURE updateProd2TechLocationType;
