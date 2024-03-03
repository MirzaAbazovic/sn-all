-- Gültig Von Datum korrigieren
update T_TECH_LEISTUNG set GUELTIG_VON = TO_DATE('06/01/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS') where ID BETWEEN 540 AND 545;
