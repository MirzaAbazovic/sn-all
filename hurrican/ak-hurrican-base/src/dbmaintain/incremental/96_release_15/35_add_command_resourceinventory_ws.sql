-- URL ist eine Annahme. Muss noch angepasst werden!!!
INSERT INTO T_WEBSERVICE_CONFIG (ID, NAME, DEST_SYSTEM, WS_URL, DESCRIPTION,VERSION)
    VALUES (12, 'Command ResourceInventory WS', 'COMMAND',
    concat((select regexp_substr(ws_url, '^[^:]*://[^:]*:[0-9]*') from T_WEBSERVICE_CONFIG where id = 5), '/resource/inventory'),
    'Command ResourceInventory WS',0);
