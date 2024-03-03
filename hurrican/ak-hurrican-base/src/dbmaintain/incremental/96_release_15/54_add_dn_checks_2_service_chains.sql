-- Fuegt allen Service Chains, die fuer Produkte, welche mindestens eine Rufnummer halten, konfiguriert sind,
-- die Checks zur Portierungskennung und AGSN hinzu.
CREATE OR REPLACE PROCEDURE add_dn_checks_2_service_chains
AS
  order_no T_SERVICE_COMMAND_MAPPING.ORDER_NO%TYPE;
  BEGIN
    FOR rec IN (SELECT
                  verlauf_chain_id
                FROM T_PRODUKT
                WHERE MIN_DN_COUNT > 0 AND VERLAUF_CHAIN_ID IS NOT NULL
                GROUP BY VERLAUF_CHAIN_ID
                ORDER BY VERLAUF_CHAIN_ID) LOOP
      SELECT
        max(order_no)
      INTO order_no
      FROM T_SERVICE_COMMAND_MAPPING
      WHERE REF_ID = rec.VERLAUF_CHAIN_ID AND REF_CLASS = 'de.augustakom.hurrican.model.cc.command.ServiceChain';

      INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
      VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextval,
              2027,
              rec.VERLAUF_CHAIN_ID,
              'de.augustakom.hurrican.model.cc.command.ServiceChain',
              order_no + 1,
              0);

      INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
      VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextval,
              2028,
              rec.VERLAUF_CHAIN_ID,
              'de.augustakom.hurrican.model.cc.command.ServiceChain',
              order_no + 2,
              0);

    END LOOP;
  END;
/

CALL add_dn_checks_2_service_chains();
DROP PROCEDURE add_dn_checks_2_service_chains;
