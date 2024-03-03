create table T_AUFTRAG_AKTION (
    ID number(19,0) not null,
    VERSION number(19,0) default 0 not null,
    AUFTRAG_ID number(19,0) not null,
    AKTION char(20) not null,
    DESIRED_EXECUTION_DATE date not null,
    CANCELLED char(1) default 0 not null
);

alter table T_AUFTRAG_AKTION add constraint T_AUFTRAG_AKTION_PK primary key (ID);

alter table T_AUFTRAG_AKTION add constraint FK_AUFTRAG_AKTION_2_AUFTRAG foreign key (AUFTRAG_ID) references T_AUFTRAG (ID);

create index IX_FK_AUFTRAG_AKTION_2_AUFTRAG on T_AUFTRAG_AKTION (AUFTRAG_ID) tablespace "I_HURRICAN";

alter table T_AUFTRAG_AKTION add constraint T_AUFTRAG_AKTION_ACTION check (AKTION in ('MODIFY_PORT'));

grant select, insert, update on T_AUFTRAG_AKTION to R_HURRICAN_USER;

grant select on T_AUFTRAG_AKTION to R_HURRICAN_READ_ONLY;

create sequence S_T_AUFTRAG_AKTION_0;

grant select on S_T_AUFTRAG_AKTION_0 to public;
