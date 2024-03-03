set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./MISTRAL_AUFTRAGPOS.txt

SELECT
ITEM_NO||';'||
ITEM__NO||';'||
ORDER__NO||';'||
CREATE_ORDER_NO||';'||
TERMINATE_ORDER_NO||';'||
IS_PLANNED||';'||
SERVICE_ELEM__NO||';'||
to_char(CHARGE_FROM,'YYYY-MM-DD')||';'||
to_char(CHARGE_TO,'YYYY-MM-DD')||';'||
to_char(CHARGED_UNTIL,'YYYY-MM-DD')||';'||
PARAMETER||';'||
QUANTITY||';'||
PRICE||';'||
CURRENCY_ID||';'||
FREETEXT
FROM AUFTRAGPOS;

spool off

set heading on
set feedback on