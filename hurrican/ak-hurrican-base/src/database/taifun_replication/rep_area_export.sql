set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./MISTRAL_AREA.txt

SELECT
    AREA_NO||';'||
    DESCRIPTION
FROM AREA;

spool off

set heading on
set feedback on