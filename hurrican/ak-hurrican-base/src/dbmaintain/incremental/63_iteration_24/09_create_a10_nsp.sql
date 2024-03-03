create table T_FTTX_A10_NSP (
    ID number(19,0) not null,
    VERSION number(19,0) default 0 not null,
    A10_NSP_NUMMER number(19,0) not null,
    A10_NSP_NAME varchar2(10) not null
);

alter table T_FTTX_A10_NSP add constraint PK_FTTX_A10_NSP primary key (ID);
alter table T_FTTX_A10_NSP add constraint UK_FTTX_A10_NSP_NUMMER unique(A10_NSP_NUMMER);
alter table T_FTTX_A10_NSP add constraint UK_FTTX_A10_NSP_NAME unique(A10_NSP_NAME);

grant select, insert, update on T_FTTX_A10_NSP to R_HURRICAN_USER;
grant select on T_FTTX_A10_NSP to R_HURRICAN_READ_ONLY;

create sequence S_T_FTTX_A10_NSP_0;
grant select on S_T_FTTX_A10_NSP_0 to public;
