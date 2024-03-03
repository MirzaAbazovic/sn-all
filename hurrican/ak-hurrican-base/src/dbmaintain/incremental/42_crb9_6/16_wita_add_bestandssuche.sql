ALTER TABLE T_MWF_GF_PRODUKT ADD (BESTANDSSUCHE_ID NUMBER(19,0));

create table T_MWF_BESTANDSSUCHE (
    ID NUMBER(19,0) not null
  , VERSION NUMBER(19,0) not null
  , ONKZ VARCHAR2(5)
  , RUFNUMMER VARCHAR2(14)
  , PRIMARY KEY (ID)
);

grant select, insert, update on  T_MWF_BESTANDSSUCHE to R_HURRICAN_USER;
grant select on  T_MWF_BESTANDSSUCHE to R_HURRICAN_READ_ONLY;

create sequence S_T_MWF_BESTANDSSUCHE_0 start with 1;
grant select on S_T_MWF_BESTANDSSUCHE_0 to public;