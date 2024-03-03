INSERT INTO T_LEISTUNG_4_DN (ID, LEISTUNG, PROVISIONING_NAME
    , BESCHREIBUNG
    , EXTERN_LEISTUNG__NO, EXTERN_SONSTIGES__NO, VERSION)
VALUES (S_T_LEISTUNG_4_DN_0.nextval, 'LM Direktruf', 'HOTLIMM'
    , 'Herstellung von Verbindungen zu einem vom Kunden gewünschten Zielanschluss ohne Wahl der Rufnummer sofern innerhalb eines Zeitraumes von ca. 15 Sekunden nach Belegung des Anschlusses keine Ziffern gewählt werden.'
    , 60100, NULL, 0);


INSERT INTO T_LEISTUNG_2_PARAMETER (LEISTUNG_ID, LEISTUNG_PARAMETER_ID)
VALUES ((SELECT ID FROM T_LEISTUNG_4_DN WHERE LEISTUNG = 'LM Direktruf'), 9);


INSERT INTO T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD
    , GUELTIG_VON, GUELTIG_BIS
    , VERWENDEN_VON, VERWENDEN_BIS
    , PARAM_VALUE, VERSION)
VALUES (S_T_LB_2_LEISTUNG_0.nextval, 1, (SELECT ID FROM T_LEISTUNG_4_DN WHERE LEISTUNG = 'LM Direktruf'), 60, 0
    , to_date(to_char(SYSDATE, 'dd.mm.yyyy'), 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy')
    , to_date(to_char(SYSDATE, 'dd.mm.yyyy'), 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy')
    , NULL, 0);

INSERT INTO T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD
    , GUELTIG_VON, GUELTIG_BIS
    , VERWENDEN_VON, VERWENDEN_BIS
    , PARAM_VALUE, VERSION)
VALUES (S_T_LB_2_LEISTUNG_0.nextval, 2, (SELECT ID FROM T_LEISTUNG_4_DN WHERE LEISTUNG = 'LM Direktruf'), 60, 0
    , to_date(to_char(SYSDATE, 'dd.mm.yyyy'), 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy')
    , to_date(to_char(SYSDATE, 'dd.mm.yyyy'), 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy')
    , NULL, 0);

INSERT INTO T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD
    , GUELTIG_VON, GUELTIG_BIS
    , VERWENDEN_VON, VERWENDEN_BIS
    , PARAM_VALUE, VERSION)
VALUES (S_T_LB_2_LEISTUNG_0.nextval, 4, (SELECT ID FROM T_LEISTUNG_4_DN WHERE LEISTUNG = 'LM Direktruf'), 60, 0
    , to_date(to_char(SYSDATE, 'dd.mm.yyyy'), 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy')
    , to_date(to_char(SYSDATE, 'dd.mm.yyyy'), 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy')
    , NULL, 0);

INSERT INTO T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD
    , GUELTIG_VON, GUELTIG_BIS
    , VERWENDEN_VON, VERWENDEN_BIS
    , PARAM_VALUE, VERSION)
VALUES (S_T_LB_2_LEISTUNG_0.nextval, 5, (SELECT ID FROM T_LEISTUNG_4_DN WHERE LEISTUNG = 'LM Direktruf'), 60, 0
    , to_date(to_char(SYSDATE, 'dd.mm.yyyy'), 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy')
    , to_date(to_char(SYSDATE, 'dd.mm.yyyy'), 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy')
    , NULL, 0);

INSERT INTO T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD
    , GUELTIG_VON, GUELTIG_BIS
    , VERWENDEN_VON, VERWENDEN_BIS
    , PARAM_VALUE, VERSION)
VALUES (S_T_LB_2_LEISTUNG_0.nextval, 6, (SELECT ID FROM T_LEISTUNG_4_DN WHERE LEISTUNG = 'LM Direktruf'), 60, 0
    , to_date(to_char(SYSDATE, 'dd.mm.yyyy'), 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy')
    , to_date(to_char(SYSDATE, 'dd.mm.yyyy'), 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy')
    , NULL, 0);
