set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./MISTRAL_CONTACT.txt

SELECT
    CONTACT_NO||';'||
    KUNDE__NO||';'||
    AUFTRAG__NO||';'||
    CONTACT_TYPE||';'||
    SALUTATION||';'||
    TITLE||';'||
    FIRSTNAME||';'||
    NAME||';'||
    STREET||';'||
    ADDRESS_LINE1||';'||
    ADDRESS_LINE2||';'||
    POSTBOX||';'||
    ZIP||';'||
    LOCATION||';'||
    PHONE||';'||
    MOBILE||';'||
    PAGER||';'||
    FAX||';'||
    EMAIL||';'||
    DESCRIPTION||';'||
    USERW||';'||
    to_char(DATEW,'YYYY-MM-DD')
    ANSPRECHPARTNER_INFO
FROM CONTACT;

spool off

set heading on
set feedback on