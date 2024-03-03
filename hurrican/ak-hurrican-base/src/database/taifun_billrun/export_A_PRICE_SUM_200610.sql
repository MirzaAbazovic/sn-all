set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./A_PRICE_SUM_200610.txt

SELECT
    KUNDE__NO||';'||
    AUFTRAG_NO||';'||
    NODE_CONN_NO||';'||
    PRICE_TYPE_NO||';'||
    BILL_SPEC_SELECTION||';'||
    replace(COST_SUM,',','.')||';'||
    replace(CALL_LENGTH_SUM,',','.')||';'||
    replace(TAX_TIME_SUM,',','.')||';'||
    NO_CALLS||';'||
    GROUP_QUALIFIER
FROM A_PRICE_SUM_200610;

spool off

set heading on
set feedback on
