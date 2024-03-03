set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./MISTRAL_DN.txt

SELECT
    DN_NO||';'||
    DN__NO||';'||
    to_char(VALID_FROM,'YYYY-MM-DD')||';'||
    to_char(VALID_TO,'YYYY-MM-DD')||';'||
    HIST_STATE||';'||
    HIST_CNT||';'||
    HIST_LAST||';'||
    PRIMARY_INSTANCE||';'||
    ORDER__NO||';'||
    BLOCK__NO||';'||
    ONKZ||';'||
    DN_BASE||';'||
    DIRECT_DIAL||';'||
    RANGE_FROM||';'||
    RANGE_TO||';'||
    DN_SIZE||';'||
    ALLOCATED_SIZE||';'||
    STATE||';'||
    to_char(FREE_DATE,'YYYY-MM-DD')||';'||
    CONNECTION_TYPE||';'||
    IS_MAIN_NUMBER||';'||
    OE__NO||';'||
    SWITCH_ID||';'||
    ACCOUNT_NST||';'||
    PORT_MODE||';'||
    to_char(WISH_DATE,'YYYY-MM-DD')||';'||
    to_char(WISH_TIME,'YYYY-MM-DD')||';'||
    LAST_CARRIER||';'||
    ACT_CARRIER||';'||
    FUTURE_CARRIER||';'||
    EDITOR_NO||';'||
    NON_BILLABLE||';'||
    replace(REMARKS,'
','')||';'||
    to_char(REAL_DATE,'YYYY-MM-DD')||';'||
    to_char(REAL_TIME_FROM,'YYYY-MM-DD')||';'||
    to_char(REAL_TIME_TO,'YYYY-MM-DD')||';'||
    to_char(PORT_DOC_SENT,'YYYY-MM-DD')||';'||
    to_char(PORT_DOC_RECEIVED,'YYYY-MM-DD')
FROM DN;

spool off

set heading on
set feedback on