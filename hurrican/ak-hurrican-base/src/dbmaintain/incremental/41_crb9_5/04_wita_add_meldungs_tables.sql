
    create table T_MWF_ABBM (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        KUNDEN_NUMMER varchar2(10) not null,
        ext_auftrags_nr varchar2(20) not null,
        vertrags_nummer varchar2(10),
        primary key (id)
    );
    grant select, insert, update on  T_MWF_ABBM to R_HURRICAN_USER;
    grant select on  T_MWF_ABBM to R_HURRICAN_READ_ONLY;

    create sequence S_T_MWF_ABBM_0 start with 1;
    grant select on S_T_MWF_ABBM_0 to public;


    create table T_MWF_ABM (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        ext_auftrags_nr varchar2(20) not null,
        kunden_nummer varchar2(10) not null,
        vertrags_nummer varchar2(10),
        verbindlicher_liefertermin date not null,
        leitung_id number(19,0) not null,
        primary key (id)
    );
    grant select, insert, update on  T_MWF_ABM to R_HURRICAN_USER;
    grant select on  T_MWF_ABM to R_HURRICAN_READ_ONLY;

    create sequence S_T_MWF_ABM_0 start with 1;
    grant select on S_T_MWF_ABM_0 to public;


    create table T_MWF_LEITUNG (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        LEITUNGSBEZEICHNUNG_ID number(19,0) not null,
        schleifen_widerstand varchar2(10),
        max_brutto_bitrate varchar2(6),
        primary key (id)
    );
    grant select, insert, update on  T_MWF_LEITUNG to R_HURRICAN_USER;
    grant select on  T_MWF_LEITUNG to R_HURRICAN_READ_ONLY;
    create sequence S_T_MWF_LEITUNG_0 start with 1;
    grant select on S_T_MWF_LEITUNG_0 to public;


    create table T_MWF_LEITUNGS_BEZEICHNUNG (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        leitungs_schluessel_zahl varchar2(4) not null,
        leitungs_schluessel_zahl_erg varchar2(5),
        onkz_a number(19,0) not null,
        onkz_b number(19,0) not null,
        ordnungs_nummer number(19,0) not null,
        primary key (id)
    );
    grant select, insert, update on  T_MWF_LEITUNGS_BEZEICHNUNG to R_HURRICAN_USER;
    grant select on  T_MWF_LEITUNGS_BEZEICHNUNG to R_HURRICAN_READ_ONLY;
    create sequence S_T_MWF_LEITUNGS_BEZEICHNUNG_0 start with 1;
    grant select on S_T_MWF_LEITUNGS_BEZEICHNUNG_0 to public;


    create table T_MWF_LEITUNGS_ABSCHNITT (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        LEITUNG_ID number(19,0) not null,
        LFD_NR number(19,0) not null,
        leitungs_laenge varchar2(5) not null,
        leitungs_durchmesser varchar2(5) not null,
        primary key (id)
    );
    grant select, insert, update on  T_MWF_LEITUNGS_ABSCHNITT to R_HURRICAN_USER;
    grant select on  T_MWF_LEITUNGS_ABSCHNITT to R_HURRICAN_READ_ONLY;
    create sequence S_T_MWF_LEITUNGS_ABSCHNITT_0 start with 1;
    grant select on S_T_MWF_LEITUNGS_ABSCHNITT_0 to public;


    alter table T_MWF_ABM
        add constraint FK_MWFABM_2_LEITUNG
        foreign key (leitung_id)
        references T_MWF_LEITUNG;

    alter table T_MWF_LEITUNG
        add constraint FK_MWFLEITUNG_2_LBZ
        foreign key (LEITUNGSBEZEICHNUNG_ID)
        references T_MWF_LEITUNGS_BEZEICHNUNG;

    alter table T_MWF_LEITUNGS_ABSCHNITT
        add constraint FK_MWFLABSCHNITT_2_LEITUNG
        foreign key (LEITUNG_ID)
        references T_MWF_LEITUNG;
