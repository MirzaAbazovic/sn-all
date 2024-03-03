create table T_WITA_SEND_LIMIT (
    ID NUMBER(19) NOT NULL
  , GESCHAEFTSFALL_TYP VARCHAR2(40) NOT NULL
  , SEND_LIMIT NUMBER(19) NOT NULL
  , LAST_CHANGE_BY VARCHAR2(50) NOT NULL
  , LAST_CHANGE_AT DATE NOT NULL
  , VERSION NUMBER(19) NOT NULL
  , CONSTRAINT UQ_WITASENDLIMIT_GF UNIQUE (GESCHAEFTSFALL_TYP)
);

ALTER TABLE T_WITA_SEND_LIMIT ADD CONSTRAINT PK_T_WITA_SEND_LIMIT PRIMARY KEY (ID);
comment on column T_WITA_SEND_LIMIT.SEND_LIMIT IS 'Anzahl maximal erlaubter Vorgaenge fuer den Geschaeftsfall (-1 == unendlich)';

grant select on T_WITA_SEND_LIMIT to R_HURRICAN_READ_ONLY;
grant select, insert, update on T_WITA_SEND_LIMIT to R_HURRICAN_USER;

create sequence S_T_WITA_SEND_LIMIT_0 start with 1;
grant select on S_T_WITA_SEND_LIMIT_0 to public;




create table T_WITA_SEND_COUNT (
    ID NUMBER(19) NOT NULL
  , GESCHAEFTSFALL_TYP VARCHAR2(40) NOT NULL
  , SENT_AT DATE NOT NULL
  , VERSION NUMBER(19) NOT NULL
);

ALTER TABLE T_WITA_SEND_COUNT ADD CONSTRAINT PK_T_WITA_SEND_COUNT PRIMARY KEY (ID);

grant select on T_WITA_SEND_COUNT to R_HURRICAN_READ_ONLY;
grant select, insert, update on T_WITA_SEND_COUNT to R_HURRICAN_USER;

create sequence S_T_WITA_SEND_COUNT_0 start with 1;
grant select on S_T_WITA_SEND_COUNT_0 to public;


