-- Der neue Alcatel Baugruppentyp braucht noch ein zusätzliches Bandbreitenprofil fuer FTTX Fon
-- Dieses Bandbreitenprofil wird vom existierenden Huawei Baugruppentyp geklont.
CREATE OR REPLACE PROCEDURE cloneDslamProfile(bgNameNeu IN VARCHAR2, bgNamealt IN VARCHAR2)
AS
  bgtAltExists NUMBER;
  bgtNeuExists NUMBER;
  profileNeuExists NUMBER;
  bgtIdAlt T_HW_BAUGRUPPEN_TYP.ID%TYPE;
  bgtIdNeu T_HW_BAUGRUPPEN_TYP.ID%TYPE;
  profileAlt T_DSLAM_PROFILE%ROWTYPE;
  profileIdNeu T_DSLAM_PROFILE.ID%TYPE;
  BEGIN
    SELECT count(1) INTO bgtAltExists FROM T_HW_BAUGRUPPEN_TYP WHERE NAME = bgNameAlt AND HW_TYPE_NAME = 'DPO';
    IF bgtAltExists = 1
    THEN
      -- Id des alten Baugruppentyps ermitteln
      SELECT
        ID INTO bgtIdAlt
      FROM T_HW_BAUGRUPPEN_TYP
      WHERE NAME = bgNameAlt AND HW_TYPE_NAME = 'DPO';

      SELECT count(1) INTO bgtNeuExists FROM T_HW_BAUGRUPPEN_TYP WHERE NAME = bgNameNeu AND HW_TYPE_NAME = 'DPO';
      IF bgtNeuExists = 1
      THEN
        -- Id des neuen Baugruppentyps ermitteln
        SELECT
          ID INTO bgtIdNeu
        FROM T_HW_BAUGRUPPEN_TYP
        WHERE NAME = bgNameNeu AND HW_TYPE_NAME = 'DPO';

        SELECT count(1) INTO profileNeuExists FROM T_DSLAM_PROFILE WHERE NAME = 'MD_1000_1000_H'
                                                                         AND BAUGRUPPEN_TYP = bgtIdNeu;

        IF profileNeuExists = 0
        THEN
          -- Altes Profil lesen
          SELECT * INTO  profileAlt
          FROM T_DSLAM_PROFILE
          WHERE NAME = 'MD_1000_1000_H'
                AND BAUGRUPPEN_TYP = bgtIdAlt;

          -- DSLAM Profil anlegen
          INSERT INTO T_DSLAM_PROFILE
          (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
           GUELTIG, VERSION, ADSL1FORCE, ENABLED_FOR_AUTOCHANGE,
           DOWNSTREAM, UPSTREAM, BAUGRUPPEN_TYP)
          VALUES
            (S_T_DSLAM_PROFILE_0.nextVal, profileAlt.NAME, profileAlt.FASTPATH, profileAlt.DOWNSTREAM_TECH_LS, profileAlt.UPSTREAM_TECH_LS,
             '1', 0, profileAlt.ADSL1FORCE, profileAlt.ENABLED_FOR_AUTOCHANGE,
             profileAlt.DOWNSTREAM, profileAlt.UPSTREAM, bgtIdNeu);

          -- Id des neuen Profils ermitteln
          SELECT ID INTO  profileIdNeu
          FROM T_DSLAM_PROFILE
          WHERE NAME = 'MD_1000_1000_H'
                AND BAUGRUPPEN_TYP = bgtIdNeu;

          -- FTTX Fon zu neuem Profil zuordnen
          INSERT INTO T_PROD_2_DSLAMPROFILE
          (PROD_ID, DSLAM_PROFILE_ID)
          VALUES (511, profileIdNeu);
        END IF;
      END IF;
    END IF;
  END;
/

-- Aufruf
call cloneDslamProfile('G-010V-P_VDSL2', 'MA5651_VDSL2');

-- Wegwerfen
drop procedure cloneDslamProfile;

-- Physiktyp korrigieren: von 800 -> 809
UPDATE T_HW_BG_TYP_2_PHYSIK_TYP
SET PHYSIKTYP_ID = 809
WHERE BAUGRUPPEN_TYP_ID = (SELECT ID
                           FROM T_HW_BAUGRUPPEN_TYP
                           WHERE NAME = 'G-010V-P_VDSL2' AND HW_TYPE_NAME = 'DPO') AND PHYSIKTYP_ID = 800;
