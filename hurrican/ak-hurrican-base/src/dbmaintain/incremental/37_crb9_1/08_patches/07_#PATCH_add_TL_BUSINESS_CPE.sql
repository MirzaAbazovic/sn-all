-- techn. Leistung Business-CPE: dient zur Berechnung der Cross Connections
INSERT INTO T_TECH_LEISTUNG
   (ID, NAME, TYP, PROD_NAME_STR, DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, DESCRIPTION, VERSION)
 VALUES
   (325, 'Business-CPE', 'XCONN', ' ', '0', '0', '0', '0', '0', '1',
    TO_DATE('02/01/2011 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
    'Glasfaser SDSL Zusatzleistung zur Berechnung der XCONN IAD statt CPE', 0);

-- techn. Leistung Business-CPE als default auf Glasfaser SDSL mappen
INSERT INTO T_PROD_2_TECH_LEISTUNG
    (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
    VALUES
    (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 541, 325, null, '1');
