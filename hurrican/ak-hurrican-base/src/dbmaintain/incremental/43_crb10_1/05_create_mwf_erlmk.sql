-- Tabelle fuer die Entitaet ErledigtMeldungKunde.java

create table T_MWF_ERLMK (
    id number(19,0) not null,
    version number(19,0) default 0 not null,
    kunden_nummer varchar2(10) not null,
    vertrags_nummer VARCHAR2 (10),
    ext_auftrags_nr varchar2(20) not null,
    primary key (id)
);

grant select, insert, update on  T_MWF_ERLMK to R_HURRICAN_USER;
grant select on  T_MWF_ERLMK to R_HURRICAN_READ_ONLY;

create sequence S_T_MWF_ERLMK_0 start with 1;
grant select on S_T_MWF_ERLMK_0 to public;