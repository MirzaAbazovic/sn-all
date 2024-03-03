--
-- Neues Leistungsb�ndel f�r VoIP-IMS erstellen
--
INSERT INTO T_LEISTUNGSBUENDEL (ID, NAME, BESCHREIBUNG)
    VALUES (24, 'VoIP-IMS', 'Leistungsbuendel fuer Maxi Glasfaser-DSL Produkte auf IMS');

--
-- Leistungen zum B�ndel hinzuf�gen
--
INSERT INTO T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
    VALUES (S_T_LB_2_LEISTUNG_0.nextval, 24, 5, 60, '1', TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), NULL);
INSERT INTO T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
    VALUES (S_T_LB_2_LEISTUNG_0.nextval, 24, 6, 60, '0', TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), NULL);
INSERT INTO T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
    VALUES (S_T_LB_2_LEISTUNG_0.nextval, 24, 4, 60, '0', TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), NULL);
INSERT INTO T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
    VALUES (S_T_LB_2_LEISTUNG_0.nextval, 24, 80, 60, '0', TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), NULL);
INSERT INTO T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
    VALUES (S_T_LB_2_LEISTUNG_0.nextval, 24, 1, 60, '1', TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), NULL);
INSERT INTO T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
    VALUES (S_T_LB_2_LEISTUNG_0.nextval, 24, 13, 60, '1', TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), NULL);
INSERT INTO T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
    VALUES (S_T_LB_2_LEISTUNG_0.nextval, 24, 56, 60, '0', TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), NULL);
INSERT INTO T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
    VALUES (S_T_LB_2_LEISTUNG_0.nextval, 24, 26, 60, '1', TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), NULL);
INSERT INTO T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
    VALUES (S_T_LB_2_LEISTUNG_0.nextval, 24, 29, 60, '1', TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), NULL);
INSERT INTO T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
    VALUES (S_T_LB_2_LEISTUNG_0.nextval, 24, 28, 60, '1', TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), '25');
INSERT INTO T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
    VALUES (S_T_LB_2_LEISTUNG_0.nextval, 24, 59, 60, '1', TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), NULL);
INSERT INTO T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
    VALUES (S_T_LB_2_LEISTUNG_0.nextval, 24, 21, 60, '0', TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), NULL);
INSERT INTO T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
    VALUES (S_T_LB_2_LEISTUNG_0.nextval, 24, 14, 60, '0', TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), NULL);
INSERT INTO T_LB_2_LEISTUNG (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, GUELTIG_VON, GUELTIG_BIS, VERWENDEN_VON, VERWENDEN_BIS, PARAM_VALUE)
    VALUES (S_T_LB_2_LEISTUNG_0.nextval, 24, 46, 60, '1', TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), TO_DATE('01.12.2011', 'DD.MM.YYYY'), TO_DATE('01.01.2200', 'DD.MM.YYYY'), '60');