-- Erweiterungen fuer den REALM

alter table T_PRODUKTGRUPPE add REALM VARCHAR2(30);
comment on column T_PRODUKTGRUPPE.REALM is 'Default REALM Bezeichnung fuer Accounts der Produktgruppe';

update T_PRODUKTGRUPPE set REALM='dsl.mnet-online.de' where ID in (4);
update T_PRODUKTGRUPPE set REALM='mdsl.mnet-online.de' where ID in (1,3,7,12,16,17,18);
update T_PRODUKTGRUPPE set REALM='ipx.dsl.de' where ID in (8);

