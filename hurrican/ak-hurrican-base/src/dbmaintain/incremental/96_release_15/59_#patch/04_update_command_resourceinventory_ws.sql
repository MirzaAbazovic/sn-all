update T_WEBSERVICE_CONFIG
  set WS_URL=concat((select regexp_substr(ws_url, '^[^:]*://[^:]*:[0-9]*') from T_WEBSERVICE_CONFIG where id = 5), '/axis/services/ResourceInventoryService')
  where id=12;