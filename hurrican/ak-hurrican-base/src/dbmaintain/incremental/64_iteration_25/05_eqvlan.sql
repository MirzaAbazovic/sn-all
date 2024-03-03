create table T_EQ_VLAN (
    ID number(19,0) not null,
    EQUIPMENT_ID number(10,0) not null,
    CVLAN_TYP VARCHAR2(10) not null,
    CVLAN NUMBER(10) not null,
    SVLAN_EKP NUMBER(10) not null,
    SVLAN_OLT NUMBER(10) not null,
    SVLAN_MDU NUMBER(10) not null,
    VERSION number(19,0) default 0 not null,
    VALID_FROM timestamp NOT NULL,
    VALID_TO timestamp NOT NULL
);

alter table T_EQ_VLAN add constraint PK_EQVLAN primary key (ID);

alter table T_EQ_VLAN add constraint FK_EQVLAN_2_EQ foreign key (EQUIPMENT_ID) references T_EQUIPMENT (EQ_ID);
create index IX_EQVLAN_2_EQ on T_EQ_VLAN (EQUIPMENT_ID) tablespace "I_HURRICAN";

alter table T_EQ_VLAN add constraint CHK_EQVLAN_CVLAN check (CVLAN_TYP in ('HSI', 'HSIPLUS', 'VOIP', 'POTS', 'IAD', 'MC', 'VOD'));

grant select, insert, update on T_EQ_VLAN to R_HURRICAN_USER;
grant select on T_EQ_VLAN to R_HURRICAN_READ_ONLY;

create sequence S_T_EQ_VLAN_0;
grant select on S_T_EQ_VLAN_0 to public;

