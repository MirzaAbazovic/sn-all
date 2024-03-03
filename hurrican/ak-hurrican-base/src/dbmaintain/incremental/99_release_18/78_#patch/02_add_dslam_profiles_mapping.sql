-- function to configure DPO DSLAM Profiles
CREATE OR REPLACE PROCEDURE add_dslam_profil_mapping(down IN NUMBER, up IN NUMBER, p_name IN VARCHAR2, prod_id_in IN NUMBER)
AS
  up_tl   NUMBER;
  down_tl NUMBER;
  map_count NUMBER;
  p_id    NUMBER;

  BEGIN
-- resolve upstream id
    SELECT ID
    INTO up_tl
    FROM T_TECH_LEISTUNG
    WHERE TYP = 'UPSTREAM' AND LONG_VALUE = up;

-- resolve downstream id
    SELECT ID
    INTO down_tl
    FROM T_TECH_LEISTUNG
    WHERE TYP = 'DOWNSTREAM' AND LONG_VALUE = down;

    FOR dpo IN (SELECT * FROM T_HW_BAUGRUPPEN_TYP WHERE HW_TYPE_NAME = 'DPO')
    LOOP
      --dbms_output.put_line('DPO: ' || dpo.id);

      -- ID des erzeugten oder bereits existierenden DSLAM-Profils
      SELECT ID
      INTO p_id
      FROM T_DSLAM_PROFILE
      WHERE BAUGRUPPEN_TYP = dpo.id AND DOWNSTREAM = down AND UPSTREAM = up AND NAME = p_name;

      SELECT count(*)
      INTO map_count
      FROM T_PROD_2_DSLAMPROFILE
      WHERE PROD_ID = prod_id_in AND DSLAM_PROFILE_ID = p_id;

      IF map_count = 0
      THEN
        --dbms_output.put_line('mapping erstellen');
	      INSERT INTO T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID) VALUES (prod_id_in, p_id);
      END IF;
    END LOOP;
  END;
/

-- Mappings fuer die Profile aus
-- 99_release_18\52_configure_sdsl_dslam_profile_for_dpo_baugruppen.sql
-- hinzufuegen

-- GF SDSL 5/5, 10/10, 25/25, 50/50, 100/100

CALL add_dslam_profil_mapping(5000,     5000, 'MD_5000_5000_H',   541);
CALL add_dslam_profil_mapping(10000,   10000, 'MD_10000_10000_H', 541);
CALL add_dslam_profil_mapping(25000,   25000, 'MD_25000_25000_H', 541);
CALL add_dslam_profil_mapping(50000,   50000, 'MD_50000_50000_H', 541);
CALL add_dslam_profil_mapping(100000, 100000, 'MD_100000_100000_H', 541);

DROP PROCEDURE add_dslam_profil_mapping;
