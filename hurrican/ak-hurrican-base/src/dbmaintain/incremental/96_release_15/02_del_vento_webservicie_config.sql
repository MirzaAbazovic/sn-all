-- Die Kommunikation mit Vento l�uft aktuelle �ber den Atlas, daher werden die entsprechenden Eintr�ge in
-- T_WEBSERVICE_CONFIG nicht mehr ben�tigt.
delete from t_webservice_config where dest_system = 'VENTO';
