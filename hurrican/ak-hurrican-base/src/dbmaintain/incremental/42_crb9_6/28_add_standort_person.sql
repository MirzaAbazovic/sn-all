create table T_MWF_STANDORT_AI (
    id number(19,0) not null,
    version number(19,0) default 0 not null,
    anrede varchar2(10) not null,
    nachname varchar2(30) not null,
    vorname varchar2(30),
    strassenname varchar2(40) not null,
    hausnummer varchar2(4) not null,
    hausnummernZusatz varchar2(5),
    postleitzahl varchar2(10) not null,
    ortsname varchar2(40) not null,
    land varchar2(2),
    primary key (id)
);

grant select, insert, update on  T_MWF_STANDORT_AI to R_HURRICAN_USER;
grant select on T_MWF_STANDORT_AI to R_HURRICAN_READ_ONLY;

create sequence S_T_MWF_STANDORT_AI_0;
grant select on S_T_MWF_STANDORT_AI_0 to public;

ALTER TABLE T_MWF_GF_PRODUKT ADD (STANDORTANSCHLUSSINHABER_ID  NUMBER(19));

alter table T_MWF_GF_PRODUKT
    add constraint FKA5F55AA5172E21
    foreign key (STANDORTANSCHLUSSINHABER_ID)
    references T_MWF_STANDORT_AI;
    