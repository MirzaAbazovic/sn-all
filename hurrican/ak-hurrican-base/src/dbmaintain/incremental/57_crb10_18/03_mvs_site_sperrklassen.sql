-- Rufnummernleistungsbuendel fuer MVS Site Produkte definieren
--   - enthaelt lediglich die Sperrklasse als Leistung

Insert into T_LEISTUNGSBUENDEL (ID, NAME, BESCHREIBUNG, VERSION)
 values (25, 'MVS Site', 'Leistungsbuendel fuer MVS Site Auftraege', 0);

Insert into T_LB_2_LEISTUNG
   (ID, LB_ID, LEISTUNG_ID, OE__NO, STANDARD, 
    GUELTIG_VON, GUELTIG_BIS, 
    VERWENDEN_VON, VERWENDEN_BIS, 
    PARAM_VALUE, VERSION)
 values
   (S_T_LB_2_LEISTUNG_0.nextVal, 25, 46, 60, '1', 
    TO_DATE('01/01/2012 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    TO_DATE('01/01/2012 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    '60', 0);
