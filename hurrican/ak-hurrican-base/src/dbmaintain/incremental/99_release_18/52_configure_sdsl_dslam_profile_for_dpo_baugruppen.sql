-- function to configure DPO DSLAM Profiles
CREATE OR REPLACE PROCEDURE add_dslam_profil_4_dpo(up IN NUMBER, down IN NUMBER, p_name IN VARCHAR2)
AS
  up_tl   NUMBER;
  down_tl NUMBER;
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


    INSERT INTO T_DSLAM_PROFILE
    (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
     GUELTIG, VERSION, ADSL1FORCE, ENABLED_FOR_AUTOCHANGE,
     DOWNSTREAM, UPSTREAM, BAUGRUPPEN_TYP)
      SELECT
        S_T_DSLAM_PROFILE_0.nextVal,
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
        bgt.ID
      FROM T_HW_BAUGRUPPEN_TYP bgt
      WHERE bgt.HW_TYPE_NAME = 'DPO';
  END;
/

--create profiles 5/5, 10/10, 25/25, 50/50, 100/100
CALL add_dslam_profil_4_dpo(5000,     5000, 'MD_5000_5000_H');
CALL add_dslam_profil_4_dpo(10000,   10000, 'MD_10000_10000_H');
CALL add_dslam_profil_4_dpo(25000,   25000, 'MD_25000_25000_H');
CALL add_dslam_profil_4_dpo(50000,   50000, 'MD_50000_50000_H');
CALL add_dslam_profil_4_dpo(100000, 100000, 'MD_100000_100000_H');

DROP PROCEDURE add_dslam_profil_4_dpo;