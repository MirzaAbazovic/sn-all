-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung von Kundensperren.
--
-- Verwendete Tabellen:
--   + T_SPERRE
--
-- Besonderheiten:
--   + die Sperrhistorie wird nach Datum/Zeit aufsteigend sortiert
--

CREATE or REPLACE FORCE VIEW V_HURRICAN_CUSTOMER_LOCKS
  AS SELECT
    l.CUSTOMER_NO as CUSTOMER__NO,
    l.ID as LOCK_PROCESS,
    r.STR_VALUE as LOCK_CATEGORY,
    NULL as LOCK_TYPE,
    'FIBU' as LOCK_DEPARTMNET,
    l.CREATED_FROM as LOCK_USER,
    l.CREATED_AT as LOCK_DATE
  FROM
    T_LOCK l
    inner join T_REFERENCE r on l.LOCK_MODE_REF_ID=r.ID
  ORDER BY l.ID asc, l.CREATED_AT asc;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_CUSTOMER_LOCKS FOR V_HURRICAN_CUSTOMER_LOCKS;

GRANT SELECT ON V_HURRICAN_CUSTOMER_LOCKS TO R_HURRICAN_BSI_VIEWS;
