insert into T_TECH_LEISTUNG (ID,
                             NAME,
                             TYP,
                             PROD_NAME_STR,
                             DESCRIPTION,
                             SNAPSHOT_REL,
                             GUELTIG_VON,
                             GUELTIG_BIS,
                             VERSION)
    VALUES (550,
            'EMF',
            'LAYER2',
            ' ',
            'es wird EMF als Layer2-Protocol benötigt',
            1,
            TO_DATE('01/02/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
            TO_DATE('01/01/2030 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
            0)
;
