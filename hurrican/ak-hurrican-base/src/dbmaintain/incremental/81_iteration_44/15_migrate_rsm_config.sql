CREATE OR REPLACE PROCEDURE mig_resourcemonitor_config
IS
BEGIN
   DECLARE
      config                    t_rs_monitor_config%ROWTYPE;
      physiktyp                 T_RS_MONITOR_CONFIG.PHYSIKTYP%TYPE;
      adsl2p           CONSTANT T_RS_MONITOR_CONFIG.PHYSIKTYP%TYPE := 513;
      adsl2p_ms        CONSTANT T_RS_MONITOR_CONFIG.PHYSIKTYP%TYPE := 516;
      adsl2p_only      CONSTANT T_RS_MONITOR_CONFIG.PHYSIKTYP%TYPE := 514;
      adsl2p_only_ms   CONSTANT T_RS_MONITOR_CONFIG.PHYSIKTYP%TYPE := 517;
   BEGIN
      FOR config IN (SELECT *
                       FROM t_rs_monitor_config
                      WHERE PHYSIKTYP IN (513, 514))
      LOOP
         physiktyp := config.PHYSIKTYP;

         IF physiktyp IS NOT NULL
         THEN
            IF physiktyp = adsl2p
            THEN
               physiktyp := adsl2p_ms;
            ELSIF physiktyp = adsl2p_only
            THEN
               physiktyp := adsl2p_only_ms;
            END IF;
         END IF;

         INSERT INTO T_RS_MONITOR_CONFIG (ID,
                                          HVT_ID_STANDORT,
                                          MONITOR_TYPE,
                                          PHYSIKTYP,
                                          PHYSIKTYP_ADD,
                                          EQ_RANG_SCHNITTSTELLE,
                                          EQ_UEVT,
                                          MIN_COUNT,
                                          ALARMIERUNG,
                                          USERW,
                                          DATEW,
                                          VERSION,
                                          DAY_COUNT,
                                          KVZ_NUMMER)
              VALUES (
                        S_T_RS_MONITOR_CONFIG_0.nextval,
                        config.HVT_ID_STANDORT,
                        config.MONITOR_TYPE,
                        physiktyp,
                        config.PHYSIKTYP_ADD,
                        config.EQ_RANG_SCHNITTSTELLE,
                        config.EQ_UEVT,
                        config.MIN_COUNT,
                        config.ALARMIERUNG,
                        'ADBF_MIG',
                        SYSDATE,
                        0,
                        config.DAY_COUNT,
                        config.KVZ_NUMMER);
      END LOOP;
   END;
END;
/

CALL mig_resourcemonitor_config();

DROP PROCEDURE mig_resourcemonitor_config;