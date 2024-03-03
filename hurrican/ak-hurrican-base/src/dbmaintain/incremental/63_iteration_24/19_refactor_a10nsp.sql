
create table T_FTTX_EKP_2_A10NSPPORT (
    EKP_FRAME_CONTRACT_ID number(19,0) not null,
    A10_NSP_PORT_ID number(19,0) not null,
    IS_DEFAULT_4_EKP char(1) not null
);

alter table T_FTTX_EKP_2_A10NSPPORT add constraint UK_FTTX_EKP_2_A10NSPPORT unique (EKP_FRAME_CONTRACT_ID, A10_NSP_PORT_ID);
alter table T_FTTX_EKP_2_A10NSPPORT add constraint FK_EKP2A10_2_EKP foreign key (EKP_FRAME_CONTRACT_ID) references T_FTTX_EKP_FRAME_CONTRACT (ID);
alter table T_FTTX_EKP_2_A10NSPPORT add constraint FK_EKP2A10_2_A10NSPPORT foreign key (A10_NSP_PORT_ID) references T_FTTX_A10_NSP_PORT (ID);

create index IX_EKP2A10_2_EKP on T_FTTX_EKP_2_A10NSPPORT (EKP_FRAME_CONTRACT_ID) tablespace "I_HURRICAN";
create index IX_EKP2A10_2_A10NSPPORT on T_FTTX_EKP_2_A10NSPPORT (A10_NSP_PORT_ID) tablespace "I_HURRICAN";

grant select, insert, update on T_FTTX_EKP_2_A10NSPPORT to R_HURRICAN_USER;
grant select on T_FTTX_EKP_2_A10NSPPORT to R_HURRICAN_READ_ONLY;

create sequence S_T_FTTX_EKP_2_A10NSPPORT_0;
grant select on S_T_FTTX_EKP_2_A10NSPPORT_0 to public;


insert into T_FTTX_EKP_2_A10NSPPORT (EKP_FRAME_CONTRACT_ID, A10_NSP_PORT_ID, IS_DEFAULT_4_EKP) 
 select 
     tmp.EKP_FRAME_CONTRACT_ID,
     tmp.ID,
     tmp.IS_DEFAULT_4_EKP
   from T_FTTX_A10_NSP_PORT tmp;

alter table T_FTTX_A10_NSP_PORT drop column EKP_FRAME_CONTRACT_ID;
alter table T_FTTX_A10_NSP_PORT drop column IS_DEFAULT_4_EKP;
   