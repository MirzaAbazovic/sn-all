set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./MISTRAL_AUFTRAG.txt

SELECT
    AUFTRAG_NO||';'||
    AUFTRAG__NO||';'||
	to_char(GUELTIG_VON,'YYYY-MM-DD')||';'||
	to_char(GUELTIG_BIS,'YYYY-MM-DD')||';'||
    HIST_STATUS||';'||
    HIST_CNT||';'||
    HIST_LAST||';'||
    PRIMARY_INSTANCE||';'||
    ATYP||';'||
    ASTATUS||';'||
    OE__NO||';'||
    KUNDE__NO||';'||
    BEARBEITER_NO||';'||
    to_char(WUNSCH_TERMIN,'YYYY-MM-DD')||';'||
    BEMERKUNGEN||';'||
    BUNDLE_ORDER_NO||';'||
    R_INFO__NO||';'||
    SAP_ID||';'||
    AP_ADDRESS_NO||';'||
    USERW||';'||
    to_char(DATEW,'YYYY-MM-DD')||';'||
    BEARBEITER_KUNDE||';'||
    BEARBEITER_KUNDE_FAX||';'||
    BEARBEITER_KUNDE_RN||';'||
    BEARBEITER_KUNDE_EMAIL||';'||
    HAENDLER__NO||';'||
    BUSINESS_UNIT||';'||
    to_char(VERTRAGSDATUM,'YYYY-MM-DD')||';'||
    OLD_SERVICE__NO||';'||
    BESTELL_ID
FROM AUFTRAG;

spool off

set heading on
set feedback on