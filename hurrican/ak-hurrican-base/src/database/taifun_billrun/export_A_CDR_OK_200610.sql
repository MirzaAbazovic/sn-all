set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./A_CDR_OK_200610_0.txt

SELECT
    CDR_FILE_NO||';'||
    FILE_OFFSET||';'||
    CONN_ID||';'||
    A_NUMBER||';'||
    B_NUMBER||';'||
    C_NUMBER||';'||
    A2_NUMBER||';'||
    CALL_DATETIME||';'||
    replace(CALL_LENGTH,',','.')||';'||
    NO_ANSWER||';'||
    FIRST_RECORD||';'||
    LAST_RECORD||';'||
    SERVICE_INFO||';'||
    CENTREX||';'||
    VPN||';'||
    CAUSE_VALUE||';'||
    IND_INTERCONN||';'||
    IND_SFC||';'||
    TRUNK_GROUP_IN||';'||
    TRUNK_GROUP_OUT||';'||
    replace(DURATION_DIAL,',','.')||';'||
    replace(DURATION_ANSWER,',','.')||';'||
    NODE_CONN_NO||';'||
    replace(TAX_TIME,',','.')||';'||
    replace(COST,',','.')||';'||
    TARIFF_TRANSIT||';'||
    USER_DEF_1||';'||
    USER_DEF_2||';'||
    USER_DEF_3||';'||
    USER_DEF_4||';'||
    USER_DEF_5||';'||
    KUNDE__NO||';'||
    AUFTRAG_NO
FROM A_CDR_OK_200610_0;

spool off

set heading on
set feedback on