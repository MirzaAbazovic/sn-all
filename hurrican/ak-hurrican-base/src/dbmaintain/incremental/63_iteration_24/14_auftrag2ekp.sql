
create table T_AUFTRAG_2_EKP_FRAME_CONTRACT (
    ID number(19,0) not null,
    VERSION number(19,0) default 0 not null,
    AUFTRAG_ID number(10) not null,
    EKP_FRAME_CONTRACT_ID number(10) not null,
    ASSIGNED_FROM date not null,
    ASSIGNED_TO date not null
);

alter table T_AUFTRAG_2_EKP_FRAME_CONTRACT add constraint PK_AUFTRAG2EKPFRAMECONTRACT primary key (ID);
alter table T_AUFTRAG_2_EKP_FRAME_CONTRACT add constraint 
  FK_A2EFC_2_AUFTRAG foreign key (AUFTRAG_ID) references T_AUFTRAG (ID);
alter table T_AUFTRAG_2_EKP_FRAME_CONTRACT add constraint 
  FK_A2EFC_2_EKPFRAMECONTRACT foreign key (EKP_FRAME_CONTRACT_ID) references T_FTTX_EKP_FRAME_CONTRACT (ID);

grant select, insert, update on T_AUFTRAG_2_EKP_FRAME_CONTRACT to R_HURRICAN_USER;
grant select on T_AUFTRAG_2_EKP_FRAME_CONTRACT to R_HURRICAN_READ_ONLY;

create sequence S_T_AUFTRAG_2_EKPFRAMECONTR_0 start with 1;
grant select on S_T_AUFTRAG_2_EKPFRAMECONTR_0 to public;
