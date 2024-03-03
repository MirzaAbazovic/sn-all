create table T_KVZ_ADRESSE (
    ID                  number(19, 0) not null,
    VERSION             number(19, 0) default 0 not null,
    HVT_ID_STANDORT     number(19, 0) not null,
    KVZ_NUMMER          varchar2(5)   not null,
    ORTSTEIL            varchar2(50)  not null,
    STRASSE             varchar2(50)  not null,
    HAUS_NR             varchar2(10)  not null,
    PLZ                 varchar2(10)  not null,
    ORT                 varchar2(50)  not null
);

alter table T_KVZ_ADRESSE add constraint PK_KVZ_ADRESSE primary key (ID);
alter table T_KVZ_ADRESSE add constraint FK_KVZ_ADRESSE_2_HVT_STANDORT foreign key (HVT_ID_STANDORT) references T_HVT_STANDORT (HVT_ID_STANDORT);
alter table T_KVZ_ADRESSE add constraint UQ_KVZ_ADRESSE_HVT_KVZ unique (HVT_ID_STANDORT, KVZ_NUMMER);

CREATE SEQUENCE S_T_KVZ_ADRESSE_0;

GRANT SELECT, INSERT, UPDATE ON T_KVZ_ADRESSE TO R_HURRICAN_USER;
GRANT SELECT ON T_KVZ_ADRESSE TO R_HURRICAN_READ_ONLY;
GRANT SELECT ON S_T_KVZ_ADRESSE_0 TO PUBLIC;