UPDATE T_CARRIER SET ITU_CARRIER_CODE ='DEU.CSURF', PORTIERUNGSKENNUNG = 'D045', COMPANY_NAME = 'Mediaport GmbH (cablesurf)'  WHERE TEXT ='Mediaport';
UPDATE T_CARRIER SET PORTIERUNGSKENNUNG = 'D153' WHERE ITU_CARRIER_CODE ='DEU.AMPLUS';
UPDATE T_CARRIER SET PORTIERUNGSKENNUNG = 'D117' WHERE ITU_CARRIER_CODE ='DEU.BISP';

DELETE FROM T_CARRIER WHERE TEXT = 'cablesurf';
