set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./MISTRAL_OE_LANG.txt

SELECT
OE_NO||';'||
LANGUAGE||';'||
NAME
FROM OE_LANG;

spool off

set heading on
set feedback on