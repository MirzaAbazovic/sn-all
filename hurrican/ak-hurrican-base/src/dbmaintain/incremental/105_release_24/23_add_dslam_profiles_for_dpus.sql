-- ANF-767: Alle DPU DSLAM Profile konfigurieren (T_DSLAM_PROFILE und T_PROD_2_DSLAMPROFILE)
-- Vorlage sind die Konfigurationen fuer die DPO G-010V-P_VDSL2
CREATE OR REPLACE PROCEDURE migrate_dpus_from_dpo
AS
  p_count   NUMBER;
  map_count NUMBER;
  p_id      NUMBER;

  BEGIN
    -- fuer alle DPU Baugruppentypen
    FOR dpu IN (SELECT * FROM T_HW_BAUGRUPPEN_TYP WHERE HW_TYPE_NAME = 'DPU')
    LOOP
      --dbms_output.put_line('DPO: ' || dpo.id);

      -- alle DPO Profile fuer die Baugruppe G-010V-P_VDSL2
      FOR dpo IN (SELECT p.* FROM T_DSLAM_PROFILE p
        INNER JOIN T_HW_BAUGRUPPEN_TYP bgt ON bgt.ID = p.BAUGRUPPEN_TYP
        WHERE bgt.HW_TYPE_NAME = 'DPO' AND bgt.NAME = 'G-010V-P_VDSL2')
      LOOP

        -- DPU Profil existiert?
        SELECT count(*)
        INTO p_count
        FROM T_DSLAM_PROFILE
        WHERE BAUGRUPPEN_TYP = dpu.id AND DOWNSTREAM = dpo.DOWNSTREAM AND UPSTREAM = dpo.UPSTREAM AND NAME = dpo.NAME;

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
              dpo.NAME,
              dpo.FASTPATH,
              dpo.DOWNSTREAM_TECH_LS,
              dpo.UPSTREAM_TECH_LS,
              '1',
              0,
              dpo.ADSL1FORCE,
              dpo.ENABLED_FOR_AUTOCHANGE,
              dpo.DOWNSTREAM,
              dpo.UPSTREAM,
              dpu.id);
        END IF;

        -- fuer alle P2DP der DPO
        FOR p2p IN (SELECT * FROM T_PROD_2_DSLAMPROFILE WHERE DSLAM_PROFILE_ID = dpo.ID)
        LOOP
          -- ID des erzeugten oder bereits existierenden DSLAM-Profils
          SELECT ID
          INTO p_id
          FROM T_DSLAM_PROFILE
          WHERE BAUGRUPPEN_TYP = dpu.ID AND DOWNSTREAM = dpo.DOWNSTREAM AND UPSTREAM = dpo.UPSTREAM AND NAME = dpo.NAME;

          -- Mapping existiert?
          SELECT count(*)
          INTO map_count
          FROM T_PROD_2_DSLAMPROFILE
          WHERE PROD_ID = p2p.PROD_ID AND DSLAM_PROFILE_ID = p_id;

          -- Mapping nur erstellen, wenn Eintrag noch nicht existiert
          IF map_count = 0
          THEN
            --dbms_output.put_line('mapping erstellen');
	          INSERT INTO T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID) VALUES (p2p.PROD_ID, p_id);
          END IF;
        END LOOP;
      END LOOP;
    END LOOP;
  END;
/

-- Migrate
CALL migrate_dpus_from_dpo();

DROP PROCEDURE migrate_dpus_from_dpo;
