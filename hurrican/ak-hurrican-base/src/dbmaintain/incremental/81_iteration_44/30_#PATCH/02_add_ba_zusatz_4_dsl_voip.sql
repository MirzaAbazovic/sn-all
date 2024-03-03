CREATE OR REPLACE PROCEDURE mig_ba_zusatz (produkt_alt    NUMBER,
                                           produkt_neu    NUMBER)
IS
BEGIN
   DECLARE
      matrix                  t_ba_zusatz%ROWTYPE;
      ba_verlauf_config_alt   t_ba_verl_config%ROWTYPE;
      ba_verlauf_config_neu   t_ba_verl_config%ROWTYPE;
   BEGIN
      FOR matrix
         IN (SELECT bz.*, BVC.ANLASS, BVC.ABT_CONFIG_ID
               FROM    t_ba_zusatz bz
                    INNER JOIN
                       t_ba_verl_config bvc
                    ON BZ.BA_VERL_CONFIG_ID = BVC.ID
              WHERE     BVC.PROD_ID = produkt_alt
                    AND BVC.GUELTIG_VON <= SYSDATE
                    AND (BVC.GUELTIG_BIS IS NULL OR BVC.GUELTIG_BIS > SYSDATE)
                    AND BZ.GUELTIG_VON <= SYSDATE
                    AND (BZ.GUELTIG_BIS IS NULL OR BZ.GUELTIG_BIS > SYSDATE))
      LOOP
         BEGIN
            SELECT *
              INTO ba_verlauf_config_alt
              FROM t_ba_verl_config bvc
             WHERE bvc.ID = matrix.BA_VERL_CONFIG_ID;
         EXCEPTION
            WHEN NO_DATA_FOUND
            THEN
               raise_application_error (
                  -20000,
                     'BA Zusatz (ID '
                  || matrix.BA_VERL_CONFIG_ID
                  || ') konnte nicht ermittelt werden!');
         END;

         BEGIN
            SELECT *
              INTO ba_verlauf_config_neu
              FROM t_ba_verl_config bvc
             WHERE     bvc.PROD_ID = produkt_neu
                   AND bvc.ANLASS = ba_verlauf_config_alt.ANLASS
                   AND bvc.ABT_CONFIG_ID =
                          ba_verlauf_config_alt.ABT_CONFIG_ID;
         EXCEPTION
            WHEN NO_DATA_FOUND
            THEN
               raise_application_error (
                  -20000,
                     'BA Zusatz (ID '
                  || matrix.BA_VERL_CONFIG_ID
                  || ') konnte nicht ermittelt werden!');
         END;

         INSERT INTO T_BA_ZUSATZ (ID,
                                  BA_VERL_CONFIG_ID,
                                  ABT_ID,
                                  HVT_GRUPPE_ID,
                                  AUCH_SM,
                                  GUELTIG_VON,
                                  GUELTIG_BIS,
                                  USERW,
                                  VERSION)
              VALUES (S_T_BA_ZUSATZ_0.NEXTVAL,
                      ba_verlauf_config_neu.ID,
                      matrix.ABT_ID,
                      matrix.HVT_GRUPPE_ID,
                      matrix.AUCH_SM,
                      TRUNC (SYSDATE),
                      TO_DATE ('01/01/2200', 'DD/MM/YYYY'),
                      'DSL_VOIP_MIG',
                      0);
      END LOOP;
   END;
END;
/

CALL mig_ba_zusatz (421, 480);

DROP PROCEDURE mig_ba_zusatz;