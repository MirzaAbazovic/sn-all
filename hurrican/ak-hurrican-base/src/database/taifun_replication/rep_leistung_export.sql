set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
set null NULL
 
spool ./MISTRAL_LEISTUNG.txt

SELECT
	LEISTUNG_NO||';'||
    LEISTUNG__NO||';'||
    to_char(GUELTIG_VON,'YYYY-MM-DD')||';'||
    to_char(GUELTIG_BIS,'YYYY-MM-DD')||';'||
    HIST_STATUS||';'||
    HIST_CNT||';'||
    HIST_LAST||';'||
    PRIMARY_INSTANCE||';'||
    GENERATE_BILLPOS||';'||
    NAME||';'||
    LEISTUNGCODE||';'||
    DIS_GROUP_NO||';'||
    BILLING_CODE||';'||
    TECH_EXPORT||';'||
    PARAM_VALUES||';'||
    OE__NO||';'||
    LER_NO||';'||
    LEISTUNGKAT||';'||
    MENGE_EDITIERBAR||';'||
    MENGE||';'||
    BEDINGUNG||';'||
    MERKMAL||';'||
    MASS||';'||
    BILLING_PRICE_SOURCE||';'||
    BILLING_QUANTITY_SOURCE||';'||
    HAUPTGRUPPE||';'||
    GRUPPE||';'||
    UNTERGRUPPE||';'||
    SAP_AUFTRAG_ID||';'||
    LEISTUNGEXTERN||';'||
    BESCHREIBUNG||';'||
    FIBU_ACCOUNT||';'||
    FIBU_TAX_TYPE||';'||
    BS_RELEVANT||';'||
    EXT_MISC__NO||';'||
    EXT_PRODUKT__NO||';'||
    EXT_LEISTUNG__NO||';'||
    CUSTOMER_CAT_NO
FROM LEISTUNG;

spool off

set heading on
set feedback on