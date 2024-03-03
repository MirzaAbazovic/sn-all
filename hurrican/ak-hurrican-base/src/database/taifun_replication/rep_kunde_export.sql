set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./MISTRAL_KUNDE.txt

SELECT
    KUNDE_NO||';'||
    KUNDE__NO||';'||
    to_char(GUELTIG_VON,'YYYY-MM-DD')||';'||
    to_char(GUELTIG_BIS,'YYYY-MM-DD')||';'||
    HIST_STATUS||';'||
    HIST_CNT||';'||
    HIST_LAST||';'||
    PRIMARY_INSTANCE||';'||
    NAME||';'||
    VORNAME||';'||
    PASSWORD||';'||
    KUNDENSEGMENT_NO||';'||
    HAUPTRUFNUMMER||';'||
    RN_GESCHAEFT||';'||
    RN_FAX||';'||
    RN_MOBILE||';'||
    EMAIL||';'||
    HOMEPAGE||';'||
    NATIONALITAET_ID||';'||
    AREA_NO||';'||
    KUNDENTYP||';'||
    COMPANY_TYPE_NO||';'||
    HAUPT_KUNDE_NO||';'||
    KUNDENBETREUER_NO||';'||
    ACCOUNT_MANAGER||';'||
    BRANCHE_NO||';'||
    LEGAL_FORM||';'||
    BONITAET_ID||';'||
    IST_KUNDE||';'||
    IST_CARRIER||';'||
    PRINT_FEATURE||';'||
    REC_SOURCE||';'||
    EXT_CUSTOMER_ID||';'||
    USERW||';'||
    to_char(DATEW,'YYYY-MM-DD')||';'||
    HANDELSREG_NR||';'||
    REGISTERGER_NR||';'||
    RESELLER_KUNDE_NO||';'||
    VIP||';'||
    AUF_BLACKLIST||';'||
    to_char(LISTENDATUM,'YYYY-MM-DD')||';'||
    IST_HAUPTKUNDE||';'||
    IST_HAENDLER||';'||
    IST_RESELLER||';'||
    IST_BETREIBER||';'||
    HAENDLER_ID||';'||
    IST_INTERESSENT||';'||
    IST_MITA_CNS||';'||
    FREIGABE_RECHSTELL||';'||
    MAHNSTOPP_ID||';'||
    REC_APPROVED||';'||
    ANZAHL_MITARB||';'||
    FERNKATASTROPHE||';'||
    BONI_INDEX||';'||
    KREDIT_LIMIT
FROM KUNDE;

spool off

set heading on
set feedback on