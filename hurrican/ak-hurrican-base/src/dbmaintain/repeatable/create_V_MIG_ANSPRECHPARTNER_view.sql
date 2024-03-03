--
-- SQL-Script, um eine View fuer die Ansprechpartner-Migration anzulegen.
-- Die View migriert nur aktive Ansprechpartner, eine historisierung ist
-- nicht mehr noetig.
--

CREATE or REPLACE FORCE VIEW V_MIG_ANSPRECHPARTNER AS
  SELECT
    a.ID AS ID,
    a.ES_ID AS ES_ID,
    a.ANSPRECHPARTNER AS ANSPRECHPARTNER
  FROM
    T_ES_ANSP a
  WHERE
    a.GUELTIG_BIS > SYSDATE
  ORDER BY a.ID ASC;
