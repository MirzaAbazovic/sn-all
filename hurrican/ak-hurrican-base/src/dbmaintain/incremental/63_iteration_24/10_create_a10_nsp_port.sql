create table T_FTTX_A10_NSP_PORT (
    ID number(19,0) not null,
    VERSION number(19,0) default 0 not null,
    A10_NSP_ID number(19,0) not null,
    VBZ_ID number(19,0) not null
);

alter table T_FTTX_A10_NSP_PORT add constraint PK_FTTX_A10_NSP_PORT primary key (ID);
alter table T_FTTX_A10_NSP_PORT add constraint FK_FTTX_A10_NSP_PORT_2_A10_NSP foreign key (A10_NSP_ID) references T_FTTX_A10_NSP (ID);
alter table T_FTTX_A10_NSP_PORT add constraint FK_FTTX_A10_NSP_PORT_2_VBZ foreign key (VBZ_ID) references T_TDN (ID);

create index IX_FTTX_A10_NSP_PORT_2_A10_NSP on T_FTTX_A10_NSP_PORT (A10_NSP_ID) tablespace "I_HURRICAN";
create index IX_FTTX_A10_NSP_PORT_2_VBZ on T_FTTX_A10_NSP_PORT (VBZ_ID) tablespace "I_HURRICAN";

grant select, insert, update on T_FTTX_A10_NSP_PORT to R_HURRICAN_USER;
grant select on T_FTTX_A10_NSP_PORT to R_HURRICAN_READ_ONLY;

create sequence S_T_FTTX_A10_NSP_PORT_0;
grant select on S_T_FTTX_A10_NSP_PORT_0 to public;
