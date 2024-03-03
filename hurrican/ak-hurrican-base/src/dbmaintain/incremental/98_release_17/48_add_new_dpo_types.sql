-- Baugruppe pruefen und anlegen, wenn sie nicht existiert
-- Die Pruefung ist notwendig, da die Baugruppen  im Testsystem aus Zeitgruenden manuell angelegt wurden
CREATE OR REPLACE PROCEDURE addBaugruppeIfNotExists(bgName IN VARCHAR2, description IN VARCHAR2)
AS
  bgtExists NUMBER;
  bgtId T_HW_BAUGRUPPEN_TYP.ID%TYPE;
  dslamId T_DSLAM_PROFILE.ID%TYPE;
  BEGIN
    SELECT count(1) INTO bgtExists FROM T_HW_BAUGRUPPEN_TYP WHERE NAME = bgName;
    IF bgtExists = 0
    THEN
      -- Baugruppe anlegen
      INSERT INTO T_HW_BAUGRUPPEN_TYP (ID, NAME, PORT_COUNT, DESCRIPTION, IS_ACTIVE, HW_SCHNITTSTELLE_NAME, HW_TYPE_NAME,
                                       HVT_TECHNIK_ID, VERSION, TUNNELING, DEF_SCHICHT2_PROTOKOLL, MAX_BANDWIDTH_DOWNSTREAM,
                                       MAX_BANDWIDTH_UPSTREAM)
      VALUES
        (S_T_HW_BAUGRUPPEN_TYP_0.nextVal, bgName, 1, description, '1', 'VDSL2', 'DPO',
         2, 0, NULL, NULL, 150000,
         50000);

      SELECT
        ID INTO bgtId
      FROM T_HW_BAUGRUPPEN_TYP
      WHERE NAME = bgName AND HW_TYPE_NAME = 'DPO';

      -- FTTB_VDSL2 Physiktyp mit Baugruppe mappen
      INSERT INTO T_HW_BG_TYP_2_PHYSIK_TYP
      (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID, VERSION)
      VALUES
        (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval, bgtId, 800, 0);

      -- DSLAM Profil anlegen
      INSERT INTO T_DSLAM_PROFILE
      (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
       GUELTIG, VERSION, ADSL1FORCE, ENABLED_FOR_AUTOCHANGE,
       DOWNSTREAM, UPSTREAM, BAUGRUPPEN_TYP)
      VALUES
        (S_T_DSLAM_PROFILE_0.nextVal, 'MD_150000_15000_H', '0', 72, 73,
         '1', 0, '0', '0',
         150000, 15000, bgtId);

      SELECT ID INTO  dslamId
      FROM T_DSLAM_PROFILE
      WHERE DOWNSTREAM_TECH_LS = 72
            AND UPSTREAM_TECH_LS = 73
            AND NAME = 'MD_150000_15000_H'
            AND BAUGRUPPEN_TYP = bgtId;

      -- FTTX DSL 512
      INSERT INTO T_PROD_2_DSLAMPROFILE
      (PROD_ID, DSLAM_PROFILE_ID)
      VALUES (512, dslamId);
      -- FTTX DSL + Fon 513
      INSERT INTO T_PROD_2_DSLAMPROFILE
      (PROD_ID, DSLAM_PROFILE_ID)
      VALUES (513, dslamId);
      -- Premium Glasfaser-DSL Doppel-Flat 540
      INSERT INTO T_PROD_2_DSLAMPROFILE
      (PROD_ID, DSLAM_PROFILE_ID)
      VALUES (540, dslamId);
      -- Glasfaser ADSL 542
      INSERT INTO T_PROD_2_DSLAMPROFILE
      (PROD_ID, DSLAM_PROFILE_ID)
      VALUES (542, dslamId);
    END IF;
  END;
/

-- Aufruf
call addBaugruppeIfNotExists('MA5651S_VDSL2', 'VDSL-Port der Huawei DPO (Ausfuehrung mit Gehaeuse)');
call addBaugruppeIfNotExists('G-010V-P_VDSL2', 'VDSL-Port der Alcatel DPO');

-- Wegwerfen
drop procedure addBaugruppeIfNotExists;
