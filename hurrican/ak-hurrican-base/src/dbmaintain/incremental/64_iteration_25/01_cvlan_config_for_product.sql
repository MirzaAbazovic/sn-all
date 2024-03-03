create table T_PROD_2_CVLAN (
    ID number(19,0) not null,
    VERSION number(19,0) default 0 not null,
    PROD_ID number(19,0) not null,
    CVLAN_TYP VARCHAR2(10) not null,
    IS_MANDATORY CHAR(1) not null
);

alter table T_PROD_2_CVLAN add constraint PK_PROD2CVLAN primary key (ID);
alter table T_PROD_2_CVLAN add constraint 
  FK_PROD2CVLAN_2_PROD foreign key (PROD_ID) references T_PRODUKT (PROD_ID);
comment on table T_PROD_2_CVLAN is 'Tabelle konfiguriert zu einem Produkt die moeglichen CVLANs';
comment on column T_PROD_2_CVLAN.IS_MANDATORY is 'Falls Flag auf TRUE gesetzt ist dieser CVLAN Typ fuer das Produkt zwingend erforderlich';

alter table T_PROD_2_CVLAN add constraint CHK_PROD2CVLAN_CVLAN check (CVLAN_TYP in ('HSI', 'HSIPLUS', 'VOIP', 'POTS', 'IAD', 'MC', 'VOD'));
alter table T_PROD_2_CVLAN add constraint UK_PROD2CVLAN unique(PROD_ID, CVLAN_TYP);

grant select, insert, update on T_PROD_2_CVLAN to R_HURRICAN_USER;
grant select on T_PROD_2_CVLAN to R_HURRICAN_READ_ONLY;

create sequence S_T_PROD_2_CVLAN_0 start with 1;
grant select on S_T_PROD_2_CVLAN_0 to public;



