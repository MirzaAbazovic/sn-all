alter table t_produkt add VIER_DRAHT char(1);
update t_produkt set Vier_Draht=1 where prod_id = 99;