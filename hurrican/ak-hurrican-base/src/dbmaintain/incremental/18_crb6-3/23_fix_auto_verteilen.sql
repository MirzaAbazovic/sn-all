-- Standorte vom Typ 'NK', 'Test-Lab', 'NMC' und 'TV' von autom. Verteilung ausgeschlossen
update t_hvt_standort set AUTO_VERTEILEN='0' where STANDORT_TYP_REF_ID in (11004,11005,11006,11007);