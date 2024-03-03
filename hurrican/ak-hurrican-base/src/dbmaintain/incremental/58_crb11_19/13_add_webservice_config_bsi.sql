-- Neuer MOnline Web-Service-Endpunkt fuer IP-Adressen
Insert into T_WEBSERVICE_CONFIG
   (ID, NAME, DEST_SYSTEM, WS_URL, DESCRIPTION, VERSION)
 Values
   (10, 'BSI visitProtocol WebService ', 'BSI', 'http://mnetcrm01.m-net.de:8080/mnet/services/createVisitProtocolRequest',
    'BSI visitProtocol WebService', 0);