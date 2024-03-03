-- alle ermittelten Port-Statistiken von FTTC_KVZ Standorten loeschen
-- (werden durch naechsten Ressourcen-Monitor Run neu aufgebaut)
delete from T_RSM_PORT_USAGE where HVT_ID_STANDORT in (
    select hvt.HVT_ID_STANDORT from T_HVT_STANDORT hvt where hvt.STANDORT_TYP_REF_ID=11013
);
