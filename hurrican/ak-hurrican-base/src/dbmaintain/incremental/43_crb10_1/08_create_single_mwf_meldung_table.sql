delete from T_MWF_MELDUNGS_POSITION;

drop table t_mwf_abbm cascade constraints;
drop table t_mwf_abm cascade constraints;
drop table t_mwf_entm cascade constraints;
drop table t_mwf_erlm cascade constraints;
drop table t_mwf_erlmk cascade constraints;
drop table t_mwf_qeb cascade constraints;
drop table t_mwf_tam cascade constraints;
drop table t_mwf_vzm cascade constraints;

drop sequence S_T_MWF_ABBM_0;
drop sequence S_T_MWF_ABM_0;
drop sequence S_T_MWF_ENTM_0;
drop sequence S_T_MWF_ERLM_0;
drop sequence S_T_MWF_ERLMK_0;
drop sequence S_T_MWF_QEB_0;
drop sequence S_T_MWF_TAM_0;
drop sequence S_T_MWF_VZM_0;

create table T_MWF_MELDUNG (
    id number(19,0) not null,
    version number(19,0) default 0 not null,
    meldungstyp varchar2(30) not null,
    kunden_nummer varchar2(10),
    vertrags_nummer VARCHAR2 (10),
    ext_auftrags_nr varchar2(20),
    VERBINDLICHER_LIEFERTERMIN timestamp,
    LEITUNG_ID decimal(19),
    ENTGELTTERMIN timestamp,
    ERLEDIGUNGSTERMIN timestamp,
    VERZOEGERUNGSTERMIN timestamp,
    primary key (id)
);

grant select, insert, update on T_MWF_MELDUNG to R_HURRICAN_USER;
grant select on  T_MWF_MELDUNG to R_HURRICAN_READ_ONLY;

create sequence S_T_MWF_MELDUNG_0 start with 1;
grant select on S_T_MWF_MELDUNG_0 to public;