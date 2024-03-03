alter table T_AUFTRAG_AKTION add (PREV_AUFTRAG_ID number(19) NULL);
alter table T_AUFTRAG_AKTION add constraint FK_PREV_AUFTRAG_ID_2_AUFTRAG foreign key(PREV_AUFTRAG_ID) references T_AUFTRAG (ID);
comment on column T_AUFTRAG_AKTION.PREV_AUFTRAG_ID is 'ID des vorherigen Auftrags im Falle eines Portwechsels';