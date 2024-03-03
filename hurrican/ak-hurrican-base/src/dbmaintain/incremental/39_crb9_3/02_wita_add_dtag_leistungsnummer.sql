alter table t_carrier_kennung add WITA_LEISTUNGS_NR VARCHAR2(10);
update t_carrier_kennung set WITA_LEISTUNGS_NR='4364' where ID in (2,4,5);
update t_carrier_kennung set WITA_LEISTUNGS_NR='4485' where ID in (3);
update t_carrier_kennung set WITA_LEISTUNGS_NR='4488' where ID in (1);
