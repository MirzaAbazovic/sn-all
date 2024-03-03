create table T_FTTX_A10_PORT_2_OLT (
    A10_NSP_PORT_ID number(19,0) not null,
    HW_RACK_OLT_ID number(19,0) not null
);

alter table T_FTTX_A10_PORT_2_OLT add constraint PK_FTTX_A10_PORT_2_OLT primary key (A10_NSP_PORT_ID, HW_RACK_OLT_ID);
alter table T_FTTX_A10_PORT_2_OLT add constraint FK_FTTX_A10_PORT_2_OLT_PORT foreign key (A10_NSP_PORT_ID) references T_FTTX_A10_NSP_PORT (ID);
alter table T_FTTX_A10_PORT_2_OLT add constraint FK_FTTX_A10_PORT_2_OLT_OLT foreign key (HW_RACK_OLT_ID) references T_HW_RACK_OLT (RACK_ID);

grant select, insert, update on T_FTTX_A10_PORT_2_OLT to R_HURRICAN_USER;
grant select on T_FTTX_A10_PORT_2_OLT to R_HURRICAN_READ_ONLY;

alter table T_FTTX_A10_NSP_PORT add (
    EKP_FRAME_CONTRACT_ID number(19,0) not null,
    IS_DEFAULT_4_EKP char(1)
);

alter table T_FTTX_A10_NSP_PORT add constraint FK_FTTX_A10_NSP_PORT_EKP foreign key (EKP_FRAME_CONTRACT_ID) references T_FTTX_EKP_FRAME_CONTRACT (ID);
