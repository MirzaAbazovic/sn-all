create table T_TRANSPONDER_GROUP (
  id number(19,0) not null,
  version number(19,0) default 0 not null,
  KUNDE__NO NUMBER(10,0) not null,
  TRANSPONDER_DESCRIPTION VARCHAR2(25) not null,
  primary key(id)
);

comment on table T_TRANSPONDER_GROUP is 'Definiert eine Gruppe von Transpondern zu einer Kundennummer';
comment on column T_TRANSPONDER_GROUP.TRANSPONDER_DESCRIPTION is 'kurze Beschreibung der Transponder-Gruppe';

grant select, insert, update on  T_TRANSPONDER_GROUP to R_HURRICAN_USER;
grant select on  T_TRANSPONDER_GROUP to R_HURRICAN_READ_ONLY;

create sequence S_T_TRANSPONDER_GROUP_0;
grant select on S_T_TRANSPONDER_GROUP_0 to public;




create table T_TRANSPONDER (
  id number(19,0) not null,
  version number(19,0) default 0 not null,
  TRANSPONDER_GROUP_ID NUMBER(19,0) not null,
  TRANSPONDER_ID NUMBER(10,0) not null,
  CUST_FIRSTNAME VARCHAR2(30),
  CUST_LASTNAME VARCHAR2(30),
  primary key(id)
);

ALTER TABLE T_TRANSPONDER ADD CONSTRAINT FK_TRANSPONDER_2_GROUP
  FOREIGN KEY (TRANSPONDER_GROUP_ID) REFERENCES T_TRANSPONDER_GROUP (ID);
  
comment on table T_TRANSPONDER is 'Zuordnung eines Transponders zu einer Transponder-Gruppe';
comment on column T_TRANSPONDER.TRANSPONDER_GROUP_ID is 'die ID der Transponder Gruppe';
comment on column T_TRANSPONDER.TRANSPONDER_ID is 'die ID des Transponders';

grant select, insert, update on  T_TRANSPONDER to R_HURRICAN_USER;
grant select on  T_TRANSPONDER to R_HURRICAN_READ_ONLY;

create sequence S_T_TRANSPONDER_0;
grant select on S_T_TRANSPONDER_0 to public;



alter table T_AUFTRAG_HOUSING_KEY add TRANSPONDER_GROUP_ID NUMBER(19,0);
alter table T_AUFTRAG_HOUSING_KEY add constraint FK_HOUSINGKEY_2_TRANSPGROUP
  foreign key (TRANSPONDER_GROUP_ID) references T_TRANSPONDER_GROUP;
alter table T_AUFTRAG_HOUSING_KEY modify TRANSPONDER_ID NUMBER(10) NULL;

