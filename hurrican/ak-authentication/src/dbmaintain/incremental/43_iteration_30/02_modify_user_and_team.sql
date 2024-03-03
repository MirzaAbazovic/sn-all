alter table TEAM drop constraint CONSTR_TEAM_NAME_DEPARTMENT;

alter table USERS add TEAM_ID number(19, 0);
alter table USERS add constraint FK_USERS_TEAM_ID foreign key (TEAM_ID) references TEAM (ID);