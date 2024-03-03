alter table T_DB_QUERIES add NOT_FOR_TEST char(1) default '0';
update T_DB_QUERIES set NOT_FOR_TEST='1' where ID=311;
