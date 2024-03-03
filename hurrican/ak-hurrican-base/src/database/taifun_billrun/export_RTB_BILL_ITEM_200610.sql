set pagesize 0
set linesize 15000
set heading off
set feedback off
set trimspool on
set echo off
set autoprint off
set termout off
 
spool ./RTB_BILL_ITEM_200610.txt

SELECT    
	ITEM_NO||';'||
    BILL_NO||';'||
    ORIG_PERIOD||';'||
    ITEM_TYPE_NO||';'||
    CUSTOMER__NO||';'||
    ORDER__NO||';'||
    PRODUCT__NO||';'||
    ORDER_ITEM__NO||';'||
    SERVICE_NO||';'||
    DISCOUNT_NO||';'||
    to_char(CHARGED_FROM,'YYYY-MM-DD')||';'||
    to_char(CHARGED_TO,'YYYY-MM-DD')||';'||
    QUANTITY||';'||
    replace(PRICE,',','.')||';'||
    replace(AMOUNT,',','.')||';'||
    CURRENCY_ID
FROM RTB_BILL_ITEM_200610;

spool off

set heading on
set feedback on