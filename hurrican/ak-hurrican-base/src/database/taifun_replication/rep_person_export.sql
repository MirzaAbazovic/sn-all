set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./MISTRAL_PERSON.txt

SELECT
    PERSON_NO||';'||
    KURZBEZ||';'||
    GESCHLECHT||';'||
    NAME||';'||
    VORNAME||';'||
    OE__NO||';'||
    STRASSE||';'||
    NUMMER||';'||
    PLZ||';'||
    ORT||';'||
    RN_GESCH||';'||
    RN_PRIV||';'||
    RN_FAX||';'||
    RN_MOBILE||';'||
    PRINTER_ID||';'||
    EMAIL
FROM PERSON;

spool off

set heading on
set feedback on