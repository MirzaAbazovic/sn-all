-- Die Kommunikation mit Vento läuft aktuelle über den Atlas, daher werden die entsprechenden Einträge in
-- T_WEBSERVICE_CONFIG nicht mehr benötigt.
delete from t_webservice_config where dest_system = 'VENTO';
