alter table T_AUFTRAG_2_TECH_LS add AKTIONS_ID_ADD NUMBER(19);
comment on column T_AUFTRAG_2_TECH_LS.AKTIONS_ID_ADD is 'Aktions-ID durch die die Leistung dem Auftrag hinzugefuegt wurde';
alter table T_AUFTRAG_2_TECH_LS add constraint FK_A2TECHLS_2_AAKTION_ADD foreign key (AKTIONS_ID_ADD) references T_AUFTRAG_AKTION (ID);

alter table T_AUFTRAG_2_TECH_LS add AKTIONS_ID_REMOVE NUMBER(19);
comment on column T_AUFTRAG_2_TECH_LS.AKTIONS_ID_REMOVE is 'Aktions-ID durch die die Leistung vom Auftrag entfernt bzw. gekuendigt wurde';
alter table T_AUFTRAG_2_TECH_LS add constraint FK_A2TECHLS_2_AAKTION_REM foreign key (AKTIONS_ID_REMOVE) references T_AUFTRAG_AKTION (ID);



alter table T_EQ_VLAN add AKTIONS_ID_ADD NUMBER(19);
comment on column T_EQ_VLAN.AKTIONS_ID_ADD is 'Aktions-ID durch die die VLAN-Daten hinzugefuegt wurden';
alter table T_EQ_VLAN add constraint FK_EQVLAN_2_AAKTION_ADD foreign key (AKTIONS_ID_ADD) references T_AUFTRAG_AKTION (ID);

alter table T_EQ_VLAN add AKTIONS_ID_REMOVE NUMBER(19);
comment on column T_EQ_VLAN.AKTIONS_ID_REMOVE is 'Aktions-ID durch die die VLAN-Daten entfernt bzw. gekuendigt wurden';
alter table T_EQ_VLAN add constraint FK_EQVLAN_2_AAKTION_REM foreign key (AKTIONS_ID_REMOVE) references T_AUFTRAG_AKTION (ID);



alter table T_AUFTRAG_2_EKP_FRAME_CONTRACT add AKTIONS_ID_ADD NUMBER(19);
comment on column T_AUFTRAG_2_EKP_FRAME_CONTRACT.AKTIONS_ID_ADD is 'Aktions-ID durch die die EKP-Zuordnung hinzugefuegt wurden';
alter table T_AUFTRAG_2_EKP_FRAME_CONTRACT add constraint FK_A2EKP_2_AAKTION_ADD foreign key (AKTIONS_ID_ADD) references T_AUFTRAG_AKTION (ID);

alter table T_AUFTRAG_2_EKP_FRAME_CONTRACT add AKTIONS_ID_REMOVE NUMBER(19);
comment on column T_AUFTRAG_2_EKP_FRAME_CONTRACT.AKTIONS_ID_REMOVE is 'Aktions-ID durch die die EKP-Zuordnung entfernt bzw. deaktiviert wurden';
alter table T_AUFTRAG_2_EKP_FRAME_CONTRACT add constraint FK_A2EKP_2_AAKTION_REM foreign key (AKTIONS_ID_REMOVE) references T_AUFTRAG_AKTION (ID);
