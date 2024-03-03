set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./MISTRAL_ADDRESS_FORMAT.txt

SELECT
FORMAT_NAME||';'||
DEFAULT_FORMAT||';'||
SHORT_FORMAT||';'||
NAME_FORMAT||';'||
SALUTATION_FORMAT
FROM ADDRESS_FORMAT;

spool off

set heading on
set feedback on