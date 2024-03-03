-- Baugruppentyp MA5651S_VDSL2 loeschen, da dieser Typ real existiert, aber von Command und Hurrican gleich wie
-- MA5651_VDSL2 behandelt wird. D.h. MA5651S bekommt in Command und Hurrican den gleichen Namen wie MA5651.
CREATE OR REPLACE PROCEDURE dropBaugruppentyp(bgName IN VARCHAR2)
AS
  bgtExists NUMBER;
  profileExists NUMBER;
  bgtId T_HW_BAUGRUPPEN_TYP.ID%TYPE;
  profileId T_DSLAM_PROFILE.ID%TYPE;
  BEGIN
    SELECT count(1) INTO bgtExists FROM T_HW_BAUGRUPPEN_TYP WHERE NAME = bgName;
    IF bgtExists = 1
    THEN
      SELECT
        ID INTO bgtId
      FROM T_HW_BAUGRUPPEN_TYP
      WHERE NAME = bgName AND HW_TYPE_NAME = 'DPO';

      SELECT count(1) INTO profileExists FROM T_DSLAM_PROFILE WHERE DOWNSTREAM_TECH_LS = 72
                                                                    AND UPSTREAM_TECH_LS = 73
                                                                    AND NAME = 'MD_150000_15000_H'
                                                                    AND BAUGRUPPEN_TYP = bgtId;
      IF profileExists = 1
      THEN
        SELECT ID INTO  profileId
        FROM T_DSLAM_PROFILE
        WHERE DOWNSTREAM_TECH_LS = 72
              AND UPSTREAM_TECH_LS = 73
              AND NAME = 'MD_150000_15000_H'
              AND BAUGRUPPEN_TYP = bgtId;

        -- Produkt zu Bandbreitenprofil Zuordnunen loeschen
        DELETE FROM T_PROD_2_DSLAMPROFILE WHERE DSLAM_PROFILE_ID = profileId;

        -- Bandbreitenprofil loeschen
        DELETE FROM T_DSLAM_PROFILE WHERE ID = profileId;
      END IF;

      -- Zuordnung Baugruppentyp zu Physiktyp loeschen
      DELETE FROM T_HW_BG_TYP_2_PHYSIK_TYP WHERE BAUGRUPPEN_TYP_ID = bgtId;

      -- Baugruppentyp loeschen
      DELETE FROM T_HW_BAUGRUPPEN_TYP WHERE ID = bgtId;
    END IF;
  END;
/

-- Aufruf
call dropBaugruppentyp('MA5651S_VDSL2');

-- Wegwerfen
drop procedure dropBaugruppentyp;
