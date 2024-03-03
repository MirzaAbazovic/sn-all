create table T_WHOLESALE_AUDIT (
    ID number(19,0) not null,
    VERSION number(19,0) default 0 not null,
	  VORABSTIMMUNGSID VARCHAR2(21 BYTE) not null,
	  BESCHREIBUNG VARCHAR2(31 CHAR),
	  BEARBEITER VARCHAR2(31 CHAR),
	  DATUM TIMESTAMP,
	  STATUS VARCHAR2(20 BYTE),
	  REQUEST_XML CLOB,
	  constraint T_WHOLESALE_AUDIT_PK primary key (ID));

grant select, insert, update on T_WHOLESALE_AUDIT to R_HURRICAN_USER;
grant select on T_WHOLESALE_AUDIT to R_HURRICAN_READ_ONLY;

create sequence S_T_WHOLESALE_AUDIT_0;
grant select on S_T_WHOLESALE_AUDIT_0 to public;
