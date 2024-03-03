alter table t_produkt add CPS_MULTI_DRAHT CHAR(1) DEFAULT '0';

update t_produkt set CPS_MULTI_DRAHT='1' where PROD_ID in (60,61,62,63,64,66,67,68,69,460);
update t_produkt set CPS_MULTI_DRAHT='0' where CPS_MULTI_DRAHT<>'1';
