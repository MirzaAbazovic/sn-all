alter table t_carrierbestellung_pv rename to t_vorabstimmung;

DECLARE
  maxval NUMBER;
BEGIN
  SELECT NVL ( (SELECT MAX (ID) + 1 FROM t_vorabstimmung), 1)
   INTO maxval
   FROM DUAL;
  EXECUTE IMMEDIATE 'create sequence S_T_VORABSTIMMUNG_0 start with ' || maxval ;
END;
/

grant select on S_T_VORABSTIMMUNG_0 to public;

drop sequence S_T_CARRIERBESTELLUNG_PV_0;

