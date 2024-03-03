-- FTTB_INT entfernen
-- FKs entfernen
delete from t_rs_monitor_config where physiktyp = 802 or physiktyp_add = 802;
delete from t_rsm_port_usage where physiktyp=802 or physiktyp_additional=802;
delete from t_rsm_rang_count where physiktyp=802 or physiktyp_additional=802;

-- Entity entfernen
delete from t_physiktyp where ID=802;