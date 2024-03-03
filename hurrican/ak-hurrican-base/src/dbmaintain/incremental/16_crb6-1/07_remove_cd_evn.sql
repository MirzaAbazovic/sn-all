-- Registry-Eintraege fuer CD EVNs entfernen
delete from t_registry where id in (1113, 1114);
