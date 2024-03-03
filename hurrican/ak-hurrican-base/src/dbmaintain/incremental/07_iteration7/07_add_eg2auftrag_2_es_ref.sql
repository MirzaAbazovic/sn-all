alter table t_eg_2_auftrag add ENDSTELLE_ID number(10);
comment on column t_eg_2_auftrag.ENDSTELLE_ID is 'Endstelle des Endgeraets';

alter table t_eg_2_auftrag add (
constraint FK_EG2A_2_ES
 foreign key (ENDSTELLE_ID)
 REFERENCES t_endstelle (ID));