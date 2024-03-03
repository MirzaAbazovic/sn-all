
Insert into T_TECH_LEISTUNG
   (ID, NAME, EXTERN_LEISTUNG__NO, TYP, PARAMETER, 
    PROD_NAME_STR, DESCRIPTION, DISPO, EWSD, SDH, 
    IPS, SCT, CHECK_QUANTITY, SNAPSHOT_REL, GUELTIG_VON, 
    GUELTIG_BIS, VERSION)
 values
   (350, 'Endgeräteport', 20020, 'EG_PORT', '2', 
    ' ', 'Endgeräteport für FTTX GK Produkte (Premium Glasfaser DSL und Glasfaser SDSL); das Parameter Feld gibt die Anzahl Sprachkanäle pro Port an', '0', '0', '0', 
    '0', '0', '1', '1', TO_DATE('01/01/2012 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0);

Insert into T_TECH_LEISTUNG
   (ID, NAME, EXTERN_LEISTUNG__NO, TYP, PARAMETER, 
    PROD_NAME_STR, DESCRIPTION, DISPO, EWSD, SDH, 
    IPS, SCT, CHECK_QUANTITY, SNAPSHOT_REL, GUELTIG_VON, 
    GUELTIG_BIS, VERSION)
 values
   (351, 'weiterer Endgeräteport', 20021, 'EG_PORT_ADD', '2', 
    ' ', 'weiterer Endgeräteport für FTTX GK Produkte (Premium Glasfaser DSL und Glasfaser SDSL); das Parameter Feld gibt die Anzahl Sprachkanäle pro Port an', '0', '0', '0', 
    '0', '0', '1', '1', TO_DATE('01/01/2012 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0);

update T_PROD_2_TECH_LEISTUNG set TECH_LS_ID=350 where TECH_LS_ID=299;

Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 351, '0', 1);

delete from t_prod_2_tech_leistung where prod_id=540 and tech_ls_id=350 and tech_ls_dependency is null;

