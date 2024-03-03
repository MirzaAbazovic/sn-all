delete from RTB_BILL_200611;
delete from A_PRICE_SUM_200611;
delete from RTB_BILL_ITEM_200611;

load data local infile "./RTB_BILL_200611.txt" into table RTB_BILL_200611
FIELDS TERMINATED BY ';' ENCLOSED BY '' ESCAPED BY '\\'
LINES TERMINATED BY '\n';

load data local infile "./A_PRICE_SUM_200611.txt" into table A_PRICE_SUM_200611
FIELDS TERMINATED BY ';' ENCLOSED BY '' ESCAPED BY '\\'
LINES TERMINATED BY '\n';

load data local infile "./RTB_BILL_ITEM_200611.txt" into table RTB_BILL_ITEM_200611
FIELDS TERMINATED BY ';' ENCLOSED BY '' ESCAPED BY '\\'
LINES TERMINATED BY '\n';


update RTB_BILL_200610 set INVOICE_DATE=null where INVOICE_DATE='0000-00-00';
update RTB_BILL_200610 set CHARGED_FROM=null where CHARGED_FROM='0000-00-00';
update RTB_BILL_200610 set CHARGED_TO=null where CHARGED_TO='0000-00-00';
update RTB_BILL_200610 set PAID_ON=null where PAID_ON='0000-00-00';
update RTB_BILL_200610 set RELEASED_AT=null where RELEASED_AT='0000-00-00';
update RTB_BILL_200610 set COMMISSIONED_AT=null where COMMISSIONED_AT='0000-00-00';
update RTB_BILL_200610 set DUNNING_LEVEL_SINCE=null where DUNNING_LEVEL_SINCE='0000-00-00';
update RTB_BILL_200610 set DUNNED_L1_AT=null where DUNNED_L1_AT='0000-00-00';
update RTB_BILL_200610 set DUNNED_L2_AT=null where DUNNED_L2_AT='0000-00-00';
update RTB_BILL_200610 set CANCELLED_AT=null where CANCELLED_AT='0000-00-00';
update RTB_BILL_200610 set WRITTEN_OFF_AT=null where WRITTEN_OFF_AT='0000-00-00';

update RTB_BILL_ITEM_200611 set CHARGED_FROM=null where CHARGED_FROM='0000-00-00';
update RTB_BILL_ITEM_200611 set CHARGED_TO=null where CHARGED_TO='0000-00-00';










