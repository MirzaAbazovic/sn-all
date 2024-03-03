set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./UDR_OK_200610.txt

SELECT
    SOURCE_SYSTEM_ID||';'||
    SS_FILE_NO||';'||
    SS_UDR_ID||';'||
    to_char(SS_UDR_DATETIME,'YYYY-MM-DD')||';'||
    SS_ORDER_ID||';'||
    SS_SERVICE_ID||';'||
    SS_BMONTH||';'||
    SS_QUANTITY||';'||
    SS_QUANTITY_UNIT||';'||
    SS_COST||';'||
    SS_COST_UNIT||';'||
    UDR_BATCH_NO||';'||
    UDR_ORDER_NO||';'||
    UDR_SERVICE_CODE||';'||
    replace(UDR_QUANTITY,',','.')||';'||
    UDR_QUANTITY_UNIT||';'||
    replace(UDR_COST',','.')||';'||
    UDR_COST_UNIT||';'||
    USER_DEF_1||';'||
    USER_DEF_2||';'||
    USER_DEF_3||';'||
    USER_DEF_4||';'||
    USER_DEF_5
FROM UDR_OK_200610;

spool off

set heading on
set feedback on