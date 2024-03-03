alter table T_AUFTRAG_2_DSLAMPROFILE add AKTIONS_ID_ADD NUMBER(19);
comment on column T_AUFTRAG_2_DSLAMPROFILE.AKTIONS_ID_ADD is 'Aktions-ID durch die das DSLAM-Profil dem Auftrag hinzugefuegt wurde';
alter table T_AUFTRAG_2_DSLAMPROFILE add constraint FK_A2DP_2_AAKTION_ADD foreign key (AKTIONS_ID_ADD) references T_AUFTRAG_AKTION (ID);

alter table T_AUFTRAG_2_DSLAMPROFILE add AKTIONS_ID_REMOVE NUMBER(19);
comment on column T_AUFTRAG_2_DSLAMPROFILE.AKTIONS_ID_REMOVE is 'Aktions-ID durch die das DSLAM-Profil vom Auftrag entfernt bzw. gekuendigt wurde';
alter table T_AUFTRAG_2_DSLAMPROFILE add constraint FK_A2DP_2_AAKTION_REM foreign key (AKTIONS_ID_REMOVE) references T_AUFTRAG_AKTION (ID);


