-- optionale switch kennung fuer ein produkt
alter table t_produkt add switch number(19,0);
alter table t_produkt add constraint fk_produkt_switch foreign key (switch) references t_reference (id);
