set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./MISTRAL_LEISTUNG_LANG.txt

SELECT
LEISTUNG_LANG_NO||';'||
VALUE||';'||
LEISTUNG_NO||';'||
LANGUAGE||';'||
NAME
FROM LEISTUNG_LANG;

spool off

set heading on
set feedback on