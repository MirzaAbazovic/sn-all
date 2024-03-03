-- Neue Dslam-Profile für doppelten Upstream anlegen
CREATE OR REPLACE PROCEDURE create_dslamprofile(profilName IN VARCHAR2, downLeistung IN NUMBER, upLeistung IN NUMBER,
                                                downstream IN NUMBER, upstream IN NUMBER, profilNameTemplate IN VARCHAR2)
IS
  BEGIN
    FOR dslamID IN (SELECT * FROM T_HW_BAUGRUPPEN_TYP
                    WHERE ID IN (select BAUGRUPPEN_TYP
                    FROM T_DSLAM_PROFILE WHERE NAME = profilNameTemplate)
    )
    LOOP
      INSERT INTO T_DSLAM_PROFILE
      (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
      GUELTIG, VERSION, ADSL1FORCE, ENABLED_FOR_AUTOCHANGE,
      DOWNSTREAM, UPSTREAM, BAUGRUPPEN_TYP)
      SELECT S_T_DSLAM_PROFILE_0.nextVal, profilName, '0', downLeistung, upLeistung, '1', 0, '0', '0', downstream, upstream, dslamID.ID
        FROM dual WHERE NOT EXISTS (SELECT * FROM T_DSLAM_PROFILE WHERE NAME = profilName AND DOWNSTREAM_TECH_LS = downLeistung
         AND UPSTREAM_TECH_LS = upLeistung AND BAUGRUPPEN_TYP = dslamID.ID);
    END LOOP;

    -- zusätzlich DefaultProfil anlegen (Baugruppen_Typ = null)
    INSERT INTO T_DSLAM_PROFILE
    (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
    GUELTIG, VERSION, ADSL1FORCE, ENABLED_FOR_AUTOCHANGE,
    DOWNSTREAM, UPSTREAM, BAUGRUPPEN_TYP)
    SELECT S_T_DSLAM_PROFILE_0.nextVal, profilName, '0', downLeistung, upLeistung, '1', 0, '0', '0', downstream, upstream, null
      FROM dual WHERE NOT EXISTS (SELECT * FROM T_DSLAM_PROFILE WHERE NAME = profilName AND DOWNSTREAM_TECH_LS = downLeistung
       AND UPSTREAM_TECH_LS = upLeistung AND BAUGRUPPEN_TYP is null);
  END;
/

--  DSLAM 25/5
call create_dslamprofile('MD_25000_5000_H',23,27,25000,5000,'MD_25000_2500_H');
-- DSLAM 50/10
call create_dslamprofile('MD_50000_10000_H',24,28,50000,10000,'MD_50000_5000_H');
-- DSLAM 100/40
call create_dslamprofile('MD_100000_40000_H',25,74,100000,40000,'MD_100000_10000_H');
-- DSLAM 150/50
call create_dslamprofile('MD_150000_50000_H',72,53,150000,50000,'MD_150000_15000_H');
-- DSLAM 300/50
call create_dslamprofile('MD_300000_50000_H',66,53,300000,50000,'MD_300000_30000_H');

DROP PROCEDURE create_dslamprofile;


-- neues PROD_2_DSLAMPROFILE- Mapping für doppelten Upstream anlegen
CREATE OR REPLACE PROCEDURE create_prod_dslam_mapping(prodId IN NUMBER, profileName IN VARCHAR2
)
IS
  BEGIN
    FOR dslamID IN (SELECT * FROM T_DSLAM_PROFILE
                    WHERE NAME = profileName
    )
    LOOP
      INSERT INTO T_PROD_2_DSLAMPROFILE
      (PROD_ID, DSLAM_PROFILE_ID)
      SELECT prodId, dslamID.ID
        FROM dual WHERE NOT EXISTS (SELECT * FROM T_PROD_2_DSLAMPROFILE WHERE PROD_ID = prodId AND DSLAM_PROFILE_ID = dslamID.ID);
    END LOOP;
  END;
/

-- SURF Flat nur bis 150 Mbit/s
call create_prod_dslam_mapping (512,'MD_25000_5000_H');
call create_prod_dslam_mapping (512,'MD_50000_10000_H');
call create_prod_dslam_mapping (512,'MD_100000_40000_H');
call create_prod_dslam_mapping (512,'MD_150000_50000_H');

-- SURF & FON Flat
call create_prod_dslam_mapping (513,'MD_25000_5000_H');
call create_prod_dslam_mapping (513,'MD_50000_10000_H');
call create_prod_dslam_mapping (513,'MD_100000_40000_H');
call create_prod_dslam_mapping (513,'MD_150000_50000_H');
call create_prod_dslam_mapping (513,'MD_300000_50000_H');

--  TAL VDSL + FON
call create_prod_dslam_mapping (514,'MD_25000_5000_H');
call create_prod_dslam_mapping (514,'MD_50000_10000_H');

-- TAL VDSL
call create_prod_dslam_mapping (515,'MD_25000_5000_H');
call create_prod_dslam_mapping (515,'MD_50000_10000_H');

DROP PROCEDURE create_prod_dslam_mapping;
