-- Premium Glasfaser-DSL Doppel-Flat 50/5 und 150/15 Bandbreitenprofile fuer Doppelten Upstream
CREATE OR REPLACE PROCEDURE createProfilesAndMapping(profileNameNeu IN VARCHAR2, profileNameAlt IN VARCHAR2,
  upstreamNeu IN NUMBER, upstreamLsNeu IN NUMBER, produktId IN NUMBER)
AS
  profileNeuExists NUMBER;
  profileNeuId T_DSLAM_PROFILE.ID%TYPE;

  BEGIN
    FOR dslamProfileAlt IN (SELECT *
                        FROM T_DSLAM_PROFILE
                        WHERE ID IN (SELECT DSLAM_PROFILE_ID
                                     FROM T_PROD_2_DSLAMPROFILE
                                     WHERE PROD_ID = produktId) AND NAME = profileNameAlt)
    LOOP
      SELECT count(1) INTO profileNeuExists
      FROM T_DSLAM_PROFILE
      WHERE NAME = profileNameNeu AND FASTPATH = dslamProfileAlt.FASTPATH AND decode(GUELTIG, dslamProfileAlt.GUELTIG, '1') = '1' AND
            decode(ADSL1FORCE, dslamProfileAlt.ADSL1FORCE, '1') = '1' AND decode(ENABLED_FOR_AUTOCHANGE, dslamProfileAlt.ENABLED_FOR_AUTOCHANGE, '1') = '1' AND
            decode(DOWNSTREAM_TECH_LS, dslamProfileAlt.DOWNSTREAM_TECH_LS, '1') = '1' AND decode(UPSTREAM_TECH_LS, upstreamLsNeu, '1') = '1' AND
            DOWNSTREAM = dslamProfileAlt.DOWNSTREAM AND decode(UPSTREAM, upstreamNeu, '1') = '1' AND
            decode(BAUGRUPPEN_TYP, dslamProfileAlt.BAUGRUPPEN_TYP, '1') = '1';
      IF profileNeuExists = 0
      THEN
        INSERT INTO T_DSLAM_PROFILE
        (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
         GUELTIG, VERSION, ADSL1FORCE, ENABLED_FOR_AUTOCHANGE,
         DOWNSTREAM, UPSTREAM, BAUGRUPPEN_TYP)
        VALUES
          (S_T_DSLAM_PROFILE_0.nextVal, profileNameNeu, dslamProfileAlt.FASTPATH, dslamProfileAlt.DOWNSTREAM_TECH_LS, upstreamLsNeu,
           '1', 0, dslamProfileAlt.ADSL1FORCE, dslamProfileAlt.ENABLED_FOR_AUTOCHANGE,
           dslamProfileAlt.DOWNSTREAM, upstreamNeu, dslamProfileAlt.BAUGRUPPEN_TYP);
      END IF;

      -- Id des neuen Profils ermitteln
      SELECT ID INTO profileNeuId
      FROM T_DSLAM_PROFILE
      WHERE NAME = profileNameNeu AND FASTPATH = dslamProfileAlt.FASTPATH AND decode(GUELTIG, dslamProfileAlt.GUELTIG, '1') = '1' AND
            decode(ADSL1FORCE, dslamProfileAlt.ADSL1FORCE, '1') = '1' AND decode(ENABLED_FOR_AUTOCHANGE, dslamProfileAlt.ENABLED_FOR_AUTOCHANGE, '1') = '1' AND
            decode(DOWNSTREAM_TECH_LS, dslamProfileAlt.DOWNSTREAM_TECH_LS, '1') = '1' AND decode(UPSTREAM_TECH_LS, upstreamLsNeu, '1') = '1' AND
            DOWNSTREAM = dslamProfileAlt.DOWNSTREAM AND decode(UPSTREAM, upstreamNeu, '1') = '1' AND
            decode(BAUGRUPPEN_TYP, dslamProfileAlt.BAUGRUPPEN_TYP, '1') = '1';

      -- Produkt zu neuem Profil zuordnen
      INSERT INTO T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
        (SELECT
           produktId,
           profileNeuId
         FROM dual
         WHERE NOT exists(SELECT *
                          FROM T_PROD_2_DSLAMPROFILE
                          WHERE PROD_ID = produktId AND
                                DSLAM_PROFILE_ID =
                                profileNeuId));
    END LOOP;
  END;
/

-- Aufruf
call createProfilesAndMapping('MD_50000_10000_H', 'MD_50000_5000_H', 10000, 28, 540);
call createProfilesAndMapping('MD_150000_30000_H', 'MD_150000_15000_H', 30000, 67, 540);

-- Wegwerfen
drop procedure createProfilesAndMapping;
