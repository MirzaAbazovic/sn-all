
Insert into DEPARTMENT
   (ID, NAME, DESCRIPTION, VERSION)
 Values
   (22, 'ZP Bereichsleitung', 'ZP Bereichsleitung', 0);

Insert into DEPARTMENT
   (ID, NAME, DESCRIPTION, VERSION)
 Values
   (23, 'TE-BE Bereichsleitung Agb', 'TE-BE Bereichsleitung Augsburg', 0);

Insert into DEPARTMENT
   (ID, NAME, DESCRIPTION, VERSION)
 Values
   (24, 'TE-BE Bereichsleitung Nbg', 'TE-BE Bereichsleitung Nürnberg', 0);

update USERS set DEP_ID=(select d.ID from DEPARTMENT d where d.NAME='TE-BE Bereichsleitung Agb') where LOGINNAME='baumannka';

Insert into USERS
   (ID, LOGINNAME, NAME, FIRSTNAME, DEP_ID,
    PASSWORD, PWDEPRECATION, EMAIL, ACTIVE, PHONE,
    FAX, PHONE_NEUTRAL, LDAP_OBJECTSID, NIEDERLASSUNG_ID, EXT_SERVICE_PROVIDER_ID,
    PROJEKTLEITER, VERSION)
 Values
   (S_USERS_0.nextVal, 'Erl', 'Erl', 'Norbert', (select d.ID from DEPARTMENT d where d.NAME='ZP Bereichsleitung'),
    'OeB58b7xtFlBQL+2NLSEIg==', TO_DATE('06/10/2010 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'norbert.erl@m-net.de', '1', '089/45200-5941',
    '089/45200-705941', '089/45200-0', NULL, 3, NULL,
    '0', 0);

Insert into USERS
   (ID, LOGINNAME, NAME, FIRSTNAME, DEP_ID,
    PASSWORD, PWDEPRECATION, EMAIL, ACTIVE, PHONE,
    FAX, PHONE_NEUTRAL, LDAP_OBJECTSID, NIEDERLASSUNG_ID, EXT_SERVICE_PROVIDER_ID,
    PROJEKTLEITER, VERSION)
 Values
   (S_USERS_0.nextVal, 'LuebkeEc', 'Lübke', 'Eckhard', (select d.ID from DEPARTMENT d where d.NAME='TE-BE Bereichsleitung Nbg'),
    'OeB58b7xtFlBQL+2NLSEIg==', TO_DATE('06/10/2010 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'norbert.erl@m-net.de', '1', '0911/1808-5400',
    '0911/1808-705400', '0911/1808-0', NULL, 4, NULL,
    '0', 0);

