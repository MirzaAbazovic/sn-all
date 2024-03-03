alter table T_PRODUKT add send_status_update char(1) default '1' not null;
update T_PRODUKT set send_status_update = '0' where prod_id in (521, 522);
