alter table T_TECH_LEISTUNG add PREVENT_AUTO_DISPATCH char(1) default '0';
update T_TECH_LEISTUNG set PREVENT_AUTO_DISPATCH='0';
update T_TECH_LEISTUNG set PREVENT_AUTO_DISPATCH='1' where ID in (602);

alter table T_TECH_LEISTUNG modify PREVENT_AUTO_DISPATCH not null;
