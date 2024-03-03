insert into T_TECH_LEISTUNG (ID,
                             NAME,
                             TYP,
                             EXTERN_LEISTUNG__NO,
                             PROD_NAME_STR,
                             DESCRIPTION,
                             SNAPSHOT_REL,
                             GUELTIG_VON,
                             GUELTIG_BIS)
VALUES (560,
        'CPE',
        'ENDGERAET',
        21000,
        ' ',
        'Endgerät für FTTX-Telefonie',
        1,
        TO_DATE('01/01/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
        TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'))
;

insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 511, 560, '0', 0);


insert into T_TECH_LEISTUNG (ID,
                             NAME,
                             TYP,
                             EXTERN_LEISTUNG__NO,
                             PROD_NAME_STR,
                             DESCRIPTION,
                             SNAPSHOT_REL,
                             GUELTIG_VON,
                             GUELTIG_BIS)
VALUES (43,
        '1000 kbit/s',
        'DOWNSTREAM',
        1000,
        '1000',
        'Downstream 1000 kbit/s',
        1,
        TO_DATE('01/01/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
        TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'))
;


insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 511, 560, 43,'0', 0);

-- Upstream 1000kbit/s
insert into T_PROD_2_TECH_LEISTUNG
(ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
Values
  (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 511, 560, 22,'0', 0);
