create table T_EKP_FRAME_CONTRACT (
    ID number(19,0) not null,
    VERSION number(19,0) default 0 not null,
    EKP_ID varchar2(10) not null,
    CONTRACT_ID varchar2(10) not null,
    CVLAN_ID number(19,0) not null
);

alter table T_EKP_FRAME_CONTRACT add constraint PK_EKP_FRAME_CONTRACT primary key (ID);
alter table T_EKP_FRAME_CONTRACT add constraint CONSTR_EKP_EKP_ID_UNIQUE unique (EKP_ID);
alter table T_EKP_FRAME_CONTRACT add constraint CONSTR_EKP_CONTRACT_ID_UNIQUE unique (CONTRACT_ID);

grant select, insert, update on T_EKP_FRAME_CONTRACT to R_HURRICAN_USER;
grant select on T_EKP_FRAME_CONTRACT to R_HURRICAN_READ_ONLY;


create table T_CVLAN (
    ID number(19,0) not null,
    VERSION number(19,0) default 0 not null,
    EKP_FRAME_CONTRACT_ID number(19,0) not null,
    TYP varchar2(10) not null,
    VLAN_VALUE varchar2(10) not null,
    PBIT varchar2(10),
    PROTOCOLL varchar2(10) not null
);

alter table T_CVLAN add constraint PK_CVLAN primary key (ID);
alter table T_CVLAN add constraint FK_CVLAN_2_EKP_FRAME_CONTRACT foreign key (EKP_FRAME_CONTRACT_ID) references T_EKP_FRAME_CONTRACT (ID);
alter table T_CVLAN add constraint CONSTR_CVLAN_TYP check (TYP in ('HSI', 'HSIPLUS', 'VOIP', 'POTS', 'IAD', 'TV'));
alter table T_CVLAN add constraint CONSTR_CVLAN_PROTOCOLL check (PROTOCOLL in ('HSI', 'HSIPLUS', 'VOIP', 'POTS', 'IAD', 'TV'));
alter table T_CVLAN add constraint CONSTR_CVLAN_CONTR_TYP_UNIQUE unique(EKP_FRAME_CONTRACT_ID, TYP);

grant select, insert, update on T_CVLAN to R_HURRICAN_USER;
grant select on T_CVLAN to R_HURRICAN_READ_ONLY;


create sequence S_T_EKP_FRAME_CONTRACT_0;
create sequence S_T_CVLAN_0;

grant select on S_T_EKP_FRAME_CONTRACT_0 to public;
grant select on S_T_CVLAN_0 to public;
