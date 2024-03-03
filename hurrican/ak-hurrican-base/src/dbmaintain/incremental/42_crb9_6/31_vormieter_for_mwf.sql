create table T_MWF_VORMIETER (
    ID NUMBER(19,0) not null
  , VERSION NUMBER(19,0) not null
  , VORNAME VARCHAR(30)
  , NACHNAME VARCHAR(30)
  , ONKZ VARCHAR(5)
  , RUFNUMMER VARCHAR(14)
  , UFANUMMER VARCHAR(14)
  , PRIMARY KEY (ID)
);

grant select, insert, update on  T_MWF_VORMIETER to R_HURRICAN_USER;
grant select on  T_MWF_VORMIETER to R_HURRICAN_READ_ONLY;

create sequence S_T_MWF_VORMIETER_0 start with 1;
grant select on S_T_MWF_VORMIETER_0 to public;

alter table T_MWF_GF_PRODUKT add VORMIETER_ID NUMBER(19,0);
alter table T_MWF_GF_PRODUKT
    add constraint FK_GF_PRODUKT_2_VORMIETER
    foreign key (VORMIETER_ID)
    references T_MWF_VORMIETER (ID);
