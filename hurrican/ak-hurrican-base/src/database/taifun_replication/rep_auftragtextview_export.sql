set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./MISTRAL_AUFTRAGTEXTVIEW.txt

SELECT
AUFTRAG_NO||';'||
TEXTTYP||';'||
EINTRAG||';'||
USERW||';'||
to_char(DATEW,'YYYY-MM-DD')
FROM AUFTRAGTEXT_VIEW;

spool off

set heading on
set feedback on