--
-- Script stellt die BSI-View auf die neue Lock-Struktur um.
--

CREATE or REPLACE FORCE VIEW VH_HURRICAN_CUSTOMER_LOCKS
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
  ORDER BY l.ID asc, l.CREATED_AT asc
;
commit;
