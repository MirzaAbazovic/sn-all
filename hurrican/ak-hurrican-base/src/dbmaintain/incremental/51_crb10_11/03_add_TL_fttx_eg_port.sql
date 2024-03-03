-- techn. Leistung FTTX Endgeräteport: dient zur Ermittlung der verfügbaren Endgeräte Ports für FTTX GK Produkte (Premium Glasfaser DSL und Glasfaser SDSL)
INSERT INTO T_TECH_LEISTUNG
   (ID, NAME, TYP, PROD_NAME_STR, DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, DESCRIPTION,
    PARAMETER, EXTERN_LEISTUNG__NO, VERSION)
 VALUES
   (299, 'Endgeräteport', 'VOIP', ' ', '0', '0', '0', '0', '0', '1',
    TO_DATE('11/07/2011 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
    'Endgeräteport für FTTX GK Produkte (Premium Glasfaser DSL und Glasfaser SDSL); das Parameter Feld gibt die Anzahl Sprachkanäle pro Port an',
    '2', 20014, 0);
