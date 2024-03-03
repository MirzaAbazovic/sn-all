alter table T_TECH_LEISTUNG add AUTO_EXPIRE char(1) default '0';
alter table T_TECH_LEISTUNG modify AUTO_EXPIRE not null;

update T_TECH_LEISTUNG set AUTO_EXPIRE='1' where ID=492;
