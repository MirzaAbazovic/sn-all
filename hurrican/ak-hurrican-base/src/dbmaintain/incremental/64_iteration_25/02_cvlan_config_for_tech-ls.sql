create table T_TECH_LEISTUNG_2_CVLAN (
    ID number(19,0) not null,
    VERSION number(19,0) default 0 not null,
    TECH_LS_ID number(19,0) not null,
    CVLAN_TYP VARCHAR2(10) not null
);

alter table T_TECH_LEISTUNG_2_CVLAN add constraint PK_TECHLS2CVLAN primary key (ID);
alter table T_TECH_LEISTUNG_2_CVLAN add constraint 
  FK_TECHLS2CVLAN_2_TECHLS foreign key (TECH_LS_ID) references T_TECH_LEISTUNG (ID);
comment on table T_TECH_LEISTUNG_2_CVLAN is 'Tabelle konfiguriert die CVLAN-Typen zu einer technischen Leistung';

alter table T_TECH_LEISTUNG_2_CVLAN add constraint CHK_TECHLS2CVLAN_CVLAN check (CVLAN_TYP in ('HSI', 'HSIPLUS', 'VOIP', 'POTS', 'IAD', 'MC', 'VOD'));
alter table T_TECH_LEISTUNG_2_CVLAN add constraint UK_TECHLS2CVLAN unique(TECH_LS_ID, CVLAN_TYP);

grant select, insert, update on T_TECH_LEISTUNG_2_CVLAN to R_HURRICAN_USER;
grant select on T_TECH_LEISTUNG_2_CVLAN to R_HURRICAN_READ_ONLY;

create sequence S_T_TECH_LEISTUNG_2_CVLAN_0 start with 1;
grant select on S_T_TECH_LEISTUNG_2_CVLAN_0 to public;



