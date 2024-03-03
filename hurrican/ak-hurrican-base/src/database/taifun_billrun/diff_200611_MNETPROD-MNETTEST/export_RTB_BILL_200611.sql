set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./RTB_BILL_200611.txt

SELECT
	BILL_NO||';'||
	BILL_ID||';'||
	to_char(INVOICE_DATE,'YYYY-MM-DD')||';'||
	CUSTOMER__NO||';'||
	ACCOUNT_NO||';'||
	to_char(CHARGED_FROM,'YYYY-MM-DD')||';'||
	to_char(CHARGED_TO,'YYYY-MM-DD')||';'||
	replace(AMOUNT,',','.')||';'||
	CURRENCY_ID||';'||
	PAYMENT_METHOD||';'||
	STATE||';'||
	replace(REMAINING_AMOUNT,',','.')||';'||
    to_char(PAID_ON,'YYYY-MM-DD')||';'||
    to_char(RELEASED_AT,'YYYY-MM-DD')||';'||
    RELEASED_BY||';'||
    to_char(COMMISSIONED_AT,'YYYY-MM-DD')||';'||
    COMMISSIONED_BY||';'||
    COMMISSION_ID||';'||
    DUNNING_LEVEL||';'||
    to_char(DUNNING_LEVEL_SINCE,'YYYY-MM-DD')||';'||
    to_char(DUNNED_L1_AT,'YYYY-MM-DD')||';'||
    DUNNED_L1_BY||';'||
    to_char(DUNNED_L2_AT,'YYYY-MM-DD')||';'||
    DUNNED_L2_BY||';'||
    CANCEL_ID||';'||
    CANCEL_CAUSE_NO||';'||
    to_char(CANCELLED_AT,'YYYY-MM-DD')||';'||
    CANCELLED_BY||';'||
    WRITE_OFF_ID||';'||
    WRITE_OFF_CAUSE_NO||';'||
    to_char(WRITTEN_OFF_AT,'YYYY-MM-DD')||';'||
    WRITTEN_OFF_BY
FROM RTB_BILL_200611;

spool off

set heading on
set feedback on