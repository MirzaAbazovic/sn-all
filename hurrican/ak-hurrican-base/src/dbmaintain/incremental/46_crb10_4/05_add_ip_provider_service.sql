-- Neuer MOnline Web-Service-Endpunkt fuer IP-Adressen
Insert into T_WEBSERVICE_CONFIG
   (ID, NAME, DEST_SYSTEM, WS_URL, DESCRIPTION, VERSION)
 Values
   (9, 'MOnline IP Provider WebService', 'MONLINE', 'http://localhost:8080/IPProviderService',
    'MOnline IP Provider WebService URL', 0);
