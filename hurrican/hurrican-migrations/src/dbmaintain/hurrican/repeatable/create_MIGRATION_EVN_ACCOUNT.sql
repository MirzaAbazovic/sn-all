--
-- View um die EVN Statuses fuer Accounts zu ermitteln
--
-- Dummy view, to be replaced upon migration data is available
--
CREATE OR REPLACE VIEW MIGRATION_EVN_ACCOUNT AS
  SELECT
    'X910033278'      AS ACCOUNT,
    1                 AS EVN_STATUS
  FROM dual
  WHERE 1 = 0   -- make it empty
;

GRANT SELECT ON MIGRATION_EVN_ACCOUNT TO R_HURRICAN_USER
;
