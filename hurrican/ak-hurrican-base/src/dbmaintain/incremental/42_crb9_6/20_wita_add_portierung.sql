ALTER TABLE T_MWF_GF_PRODUKT ADD (RUFNUMMERNPORTIERUNG_ID NUMBER(19,0));

create table T_MWF_PORTIERUNG (
    ID NUMBER(19,0) not null
  , VERSION NUMBER(19,0) not null
  , PORTIERUNGSKENNER VARCHAR2(10)
  , PORTIERUNGSTYP VARCHAR2(20)
  , ALLERUFNUMMERN VARCHAR2(1)
  , PRIMARY KEY (ID)
);

grant select, insert, update on  T_MWF_PORTIERUNG to R_HURRICAN_USER;
grant select on  T_MWF_PORTIERUNG to R_HURRICAN_READ_ONLY;

create sequence S_T_MWF_PORTIERUNG_0 start with 1;
grant select on S_T_MWF_PORTIERUNG_0 to public;

create table T_MWF_PORT_EINZELRUFNUMMER (
    ID NUMBER(19,0) not null
  , VERSION NUMBER(19,0) not null
  , PORTIERUNGS_ID NUMBER(19,0) not null
  , ONKZ VARCHAR2(5)
  , RUFNUMMER VARCHAR2(14)
  , PRIMARY KEY (ID)
);

grant select, insert, update on  T_MWF_PORT_EINZELRUFNUMMER to R_HURRICAN_USER;
grant select on  T_MWF_PORT_EINZELRUFNUMMER to R_HURRICAN_READ_ONLY;

create sequence S_T_MWF_PORT_ERN_0 start with 1;
grant select on S_T_MWF_PORT_ERN_0 to public;