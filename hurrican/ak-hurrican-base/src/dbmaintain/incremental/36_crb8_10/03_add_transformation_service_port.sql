-- Neuer Vento Web-Service-Endpunkt fuer Transformationen
Insert into T_WEBSERVICE_CONFIG
   (ID, NAME, DEST_SYSTEM, WS_URL, DESCRIPTION, VERSION)
 Values
   (8, 'Vento Transformation WebService', 'VENTO', 'http://mnetesbintern00:19090/TransformationServicePort',
    'Vento Transfromation WebService URL', 0);
