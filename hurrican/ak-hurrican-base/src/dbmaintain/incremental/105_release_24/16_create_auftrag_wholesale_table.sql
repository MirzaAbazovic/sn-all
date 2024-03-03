create table T_AUFTRAG_WHOLESALE (
    ID number(19,0) not null,
    VERSION number(19,0) default 0 not null,
    AUFTRAG_ID number(19,0) not null UNIQUE,
    WHOLESALE_AUFTRAG_ID VARCHAR2(42 CHAR),
    EXECUTION_DATE DATE,
	  EXECUTION_TIME_BEGIN VARCHAR2(8 CHAR),
	  EXECUTION_TIME_END VARCHAR2(8 CHAR),
	  constraint T_AUFTRAG_WHOLESALE_PK primary key (ID));

alter table T_AUFTRAG_WHOLESALE add constraint FK_AUFTRAG_WHOLESALE_2_AUFTRAG foreign key (AUFTRAG_ID) references T_AUFTRAG (ID);

grant select, insert, update on T_AUFTRAG_WHOLESALE to R_HURRICAN_USER;
grant select on T_AUFTRAG_WHOLESALE to R_HURRICAN_READ_ONLY;

create sequence S_T_AUFTRAG_WHOLESALE_0;
grant select on S_T_AUFTRAG_WHOLESALE_0 to public;