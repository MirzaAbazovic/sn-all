set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./MISTRAL_ACCOUNT.txt

SELECT
    AUFTRAG_NO||';'||
    ACCOUNT_ID||';'||
    ACCOUNTNAME||';'||
    USERW||';'||
    to_char(DATEW,'YYYY-MM-DD')
FROM ACCOUNT;

spool off

set heading on
set feedback on