create table TEAM (
    ID              number(19, 0) not null,
    VERSION         number(19, 0) default 0 not null,
    TEAM_NAME       varchar(30) not null,
    DEPARTMENT_ID   number(19, 0) not null
);

alter table TEAM add constraint PK_TEAM primary key (ID);
alter table TEAM add constraint FK_TEAM_2_DEPARTMENT foreign key (DEPARTMENT_ID) references DEPARTMENT (ID);
alter table TEAM add constraint CONSTR_TEAM_NAME_DEPARTMENT unique (TEAM_NAME, DEPARTMENT_ID);

grant select, insert, update on TEAM to AUTHUSER;
grant select on TEAM to AUTHUSER;

create sequence S_TEAM_0;

grant select on S_TEAM_0 to public;
