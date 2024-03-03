create table T_CBV_AUTOMATION_ERROR (
    ID number(19,0) not null,
    VERSION number(19,0) default 0 not null,
    CBVORGANG_ID number(19,0) not null,
    DATE_OCCURED date not null,
    ERROR_MESSAGE varchar2(2048),
    STACKTRACE CLOB
);

alter table T_CBV_AUTOMATION_ERROR add constraint PK_CBV_AUTOMATION_ERROR primary key (ID);
alter table T_CBV_AUTOMATION_ERROR add constraint
  FK_CBVAUTOMERROR_2_CBV foreign key (CBVORGANG_ID) references T_CB_VORGANG (ID);

grant select, insert, update on T_CBV_AUTOMATION_ERROR to R_HURRICAN_USER;
grant select on T_CBV_AUTOMATION_ERROR to R_HURRICAN_READ_ONLY;

create sequence S_T_CBV_AUTOMATION_ERROR_0 start with 1;
grant select on S_T_CBV_AUTOMATION_ERROR_0 to public;

