set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./MISTRAL_ADRESSE.txt

SELECT
    ADRESSE_NO||';'||
    KUNDE_NO||';'||
    IS_KUNDE||';'||
    IS_KORRESPONDENZ||';'||
    ANREDE||';'||
    VORNAME||';'||
    NAME||';'||
    TITLE||';'||
    NAME_ADD||';'||
    STRASSE||';'||
    STREET_ADD||';'||
    POSTFACH||';'||
    PLZ||';'||
    ORT||';'||
    LOCATION_ADD||';'||
    FORMAT||';'||
    HIST_NAME_ZUSATZ||';'||
    VORSATZWORT||';'||
    HIST_NAME_ZUSATZ2||';'||
    TITEL2||';'||
    VORNAME2||';'||
    VORSATZWORT2||';'||
    NAME2||';'||
    SONST_NAME_ZUSATZ||';'||
    NUMMER||';'||
    HAUSNUMMER_ZUSATZ||';'||
    ANSPRECHPARTNER
FROM ADRESSE;

spool off

set heading on
set feedback on