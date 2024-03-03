-- function to configure DPO DSLAM Profiles
CREATE OR REPLACE PROCEDURE add_dslam_profil_4_dpo(down IN NUMBER, up IN NUMBER, p_name IN VARCHAR2, prod_id_in IN NUMBER)
AS
  up_tl   NUMBER;
  down_tl NUMBER;
  p_count NUMBER;
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

      SELECT count(*)
      INTO p_count
      FROM T_DSLAM_PROFILE
      WHERE BAUGRUPPEN_TYP = dpo.id AND DOWNSTREAM = down AND UPSTREAM = up AND NAME = p_name;

      -- DSLAM-Profil nur erstellen, wenn es noch nicht existiert.
      IF p_count = 0
      THEN
        --dbms_output.put_line('profil erstellen');
        INSERT INTO T_DSLAM_PROFILE
          (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
          GUELTIG, VERSION, ADSL1FORCE, ENABLED_FOR_AUTOCHANGE,
          DOWNSTREAM, UPSTREAM, BAUGRUPPEN_TYP)
          VALUES
          (S_T_DSLAM_PROFILE_0.nextVal,
            p_name,
            '0',
            down_tl,
            up_tl,
            '1',
            0,
            '0',
            '0',
            down,
            up,
            dpo.id);
      END IF;

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

-- Surf&Fon 10/1, 25/2,5, 50/5 und 100/10
CALL add_dslam_profil_4_dpo(10000,   1000, 'MD_10000_1000_H',   513);
CALL add_dslam_profil_4_dpo(25000,   2500, 'MD_25000_2500_H',   513);
CALL add_dslam_profil_4_dpo(50000,   5000, 'MD_50000_5000_H',   513);
CALL add_dslam_profil_4_dpo(100000, 10000, 'MD_100000_10000_H', 513);

CALL add_dslam_profil_4_dpo(10000,   1000, 'MD_10000_1000_H',   514);
CALL add_dslam_profil_4_dpo(25000,   2500, 'MD_25000_2500_H',   514);
CALL add_dslam_profil_4_dpo(50000,   5000, 'MD_50000_5000_H',   514);
CALL add_dslam_profil_4_dpo(100000, 10000, 'MD_100000_10000_H', 514);

-- Premium 18/1, 50/5, 50/6, 50/10, 100/10, 100/20
CALL add_dslam_profil_4_dpo(18000,   1000, 'MD_18000_1000_H',   540);
CALL add_dslam_profil_4_dpo(50000,   5000, 'MD_50000_5000_H',   540);
CALL add_dslam_profil_4_dpo(50000,   6000, 'MD_50000_6000_H',   540);
CALL add_dslam_profil_4_dpo(50000,  10000, 'MD_50000_10000_H',  540);
CALL add_dslam_profil_4_dpo(100000, 10000, 'MD_100000_10000_H', 540);
CALL add_dslam_profil_4_dpo(100000, 20000, 'MD_100000_20000_H', 540);

DROP PROCEDURE add_dslam_profil_4_dpo;
