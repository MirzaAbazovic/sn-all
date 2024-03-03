set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./MISTRAL_DN_BLOCK.txt

SELECT
    BLOCK_NO||';'||
    BLOCK__NO||';'||
    to_char(VALID_FROM,'YYYY-MM-DD')||';'||
    to_char(VALID_TO,'YYYY-MM-DD')||';'||
    HIST_STATE||';'||
    HIST_CNT||';'||
    HIST_LAST||';'||
    PRIMARY_INSTANCE||';'||
    ONKZ||';'||
    BLOCK_ID||';'||
    LENGTH||';'||
    to_char(PUBLISH_DATE,'YYYY-MM-DD')||';'||
    CARRIER||';'||
    PARTITIONED||';'||
replace(REMARKS,'
','')
FROM DN_BLOCK;

spool off

set heading on
set feedback on