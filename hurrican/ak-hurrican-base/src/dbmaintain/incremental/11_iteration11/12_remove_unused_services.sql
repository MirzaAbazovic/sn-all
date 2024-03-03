
delete from t_gui_mapping where gui_id=208;
delete from t_gui_definition where id=208;

drop table T_AUFTRAG_FAKTURA cascade constraints;
drop table T_EQ_DATEN cascade constraints;
drop table T_EQ_GEBAEUDE cascade constraints;
drop table T_EQ_OBJEKT cascade constraints;

drop table T_FAKTURA cascade constraints;
drop table T_FAKTURA_BEARBEITER cascade constraints;
drop table T_FAKTURA_MONAT cascade constraints;
drop table T_FAKTURA_NO cascade constraints;
drop table T_FAKTURA_NR cascade constraints;

