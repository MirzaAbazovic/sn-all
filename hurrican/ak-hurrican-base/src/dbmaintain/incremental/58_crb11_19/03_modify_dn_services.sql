-- Leistung 'Sperrklasse f�r eingehende Verbindungen' aus neuen Leistungsbuendeln entfernen
delete from t_lb_2_leistung where lb_id in (16,17) and leistung_id=58;