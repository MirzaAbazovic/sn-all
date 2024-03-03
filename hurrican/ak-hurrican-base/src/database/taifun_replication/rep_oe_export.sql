set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./MISTRAL_OE.txt

SELECT
    OE_NO||';'||
    OE__NO||';'||
    VATER_OE_NO||';'||
    VORG_OE_NO||';'||
	to_char(GUELTIG_VON,'YYYY-MM-DD')||';'||
	to_char(GUELTIG_BIS,'YYYY-MM-DD')||';'||
    HIST_STATUS||';'||
    PRIMARY_INSTANCE||';'||
    HIST_CNT||';'||
    HIST_LAST||';'||
    OETYP||';'||
    PRODUKTCODE||';'||
    PRODUCT_ID||';'||
    BILLING_CODE||';'||
    NAME||';'||
    MINDESTUMSATZ||';'||
    MINDESTVERTRAGSDAUER||';'||
    PRODUKTEXTERN||';'||
    AUFTRAG_ZUSATZ||';'||
    AUFTRAG_KLASSE||';'||
    replace(BESCHREIBUNG,'
','')||';'||
    FIBU_PRODUCT_TYPE||';'||
    FIBU_COST_OBJECT||';'||
    INVOICE_FEATURE
FROM OE;

spool off

set heading on
set feedback on