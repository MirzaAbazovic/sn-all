
    create table T_MWF_ANSPRECHPARTNER (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        anrede varchar2(1),
        nachname varchar2(30),
        vorname varchar2(30),
        telefonnummer varchar2(30),
        email varchar2(320),
        rolle varchar2(30),
        primary key (id)
    );
    grant select, insert, update on  T_MWF_ANSPRECHPARTNER to R_HURRICAN_USER;
    grant select on  T_MWF_ANSPRECHPARTNER to R_HURRICAN_READ_ONLY;

    create table T_MWF_AUFTRAG (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        CB_VORGANG_ID number(10,0) not null,
        primary key (id)
    );
    grant select, insert, update on T_MWF_AUFTRAG  to R_HURRICAN_USER;
    grant select on  T_MWF_AUFTRAG to R_HURRICAN_READ_ONLY;

    create table T_MWF_AUFTRAGSMANAGEMENT (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        anrede varchar2(1),
        nachname varchar2(30),
        vorname varchar2(30),
        telefonnummer varchar2(30),
        email varchar2(320),
        primary key (id)
    );
    grant select, insert, update on  T_MWF_AUFTRAGSMANAGEMENT to R_HURRICAN_USER;
    grant select on  T_MWF_AUFTRAGSMANAGEMENT to R_HURRICAN_READ_ONLY;

    create table T_MWF_AUFTRAGSPOSITION (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        MWF_GESCHAEFTSFALL_ID number(19,0) not null,
        produkt varchar2(100) not null,
        produktBezeichner varchar2(10) not null,
        primary key (id)
    );
    grant select, insert, update on T_MWF_AUFTRAGSPOSITION  to R_HURRICAN_USER;
    grant select on T_MWF_AUFTRAGSPOSITION to R_HURRICAN_READ_ONLY;

    create table T_MWF_GESCHAEFTSFALL (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        MWF_AUFTRAG_ID number(19,0) not null,
        geschaeftsfalltyp varchar2(30) not null,
        primary key (id)
    );
    grant select, insert, update on T_MWF_GESCHAEFTSFALL to R_HURRICAN_USER;
    grant select on T_MWF_GESCHAEFTSFALL to R_HURRICAN_READ_ONLY;

    create table T_MWF_GF_ANSPRECHPARTNER (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        MWF_GESCHAEFTSFALL_ID number(19,0) not null,
        MWF_ANSPRECHPARTNER_ID number(19,0) not null,
        MWF_AUFTRAGSMANAGEMENT_ID number(19,0) not null,
        primary key (id)
    );
    grant select, insert, update on T_MWF_GF_ANSPRECHPARTNER to R_HURRICAN_USER;
    grant select on T_MWF_GF_ANSPRECHPARTNER to R_HURRICAN_READ_ONLY;

    create table T_MWF_GF_PRODUKT (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        MWF_AUFTRAGSPOSITION_ID number(19,0) not null,
        primary key (id)
    );
    grant select, insert, update on T_MWF_GF_PRODUKT to R_HURRICAN_USER;
    grant select on T_MWF_GF_PRODUKT to R_HURRICAN_READ_ONLY;

    create table T_MWF_KUNDE (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        kundennummer varchar2(10) not null,
        leistungsnummer varchar2(10) not null,
        MWF_AUFTRAG_ID number(19,0) not null,
        primary key (id)
    );
    grant select, insert, update on T_MWF_KUNDE  to R_HURRICAN_USER;
    grant select on T_MWF_KUNDE to R_HURRICAN_READ_ONLY;

    create table T_MWF_KUNDENWUNSCHTERMIN (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        MWF_GESCHAEFTSFALL_ID number(19,0),
        datum date not null,
        zeitfenster varchar2(6),
        primary key (id)
    );
    grant select, insert, update on T_MWF_KUNDENWUNSCHTERMIN to R_HURRICAN_USER;
    grant select on T_MWF_KUNDENWUNSCHTERMIN to R_HURRICAN_READ_ONLY;

    create table T_MWF_MONTAGELEISTUNG (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        MWF_GF_PRODUKT_ID number(19,0) not null,
        anrede varchar2(1),
        nachname varchar2(30),
        vorname varchar2(30),
        telefonnummer varchar2(30),
        emailadresse varchar2(320),
        montagehinweis varchar2(160),
        primary key (id)
    );
    grant select, insert, update on T_MWF_MONTAGELEISTUNG  to R_HURRICAN_USER;
    grant select on T_MWF_MONTAGELEISTUNG to R_HURRICAN_READ_ONLY;

    create table T_MWF_SCHALTANGABEN (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        MWF_GF_PRODUKT_ID number(19,0) not null,
        primary key (id)
    );
    grant select, insert, update on T_MWF_SCHALTANGABEN to R_HURRICAN_USER;
    grant select on T_MWF_SCHALTANGABEN to R_HURRICAN_READ_ONLY;

    create table T_MWF_SCHALTUNG_KUPFER (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        MWF_SCHALTANGABEN_ID number(19,0) not null,
        uebertragungsverfahren varchar2(3) not null,
        UEVT varchar2(4) not null,
        EVS varchar2(2) not null,
        doppelader varchar2(2) not null,
        primary key (id)
    );
    grant select, insert, update on T_MWF_SCHALTUNG_KUPFER to R_HURRICAN_USER;
    grant select on T_MWF_SCHALTUNG_KUPFER to R_HURRICAN_READ_ONLY;

    create table T_MWF_STANDORT_KOLLOKATION (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        MWF_GF_PRODUKT_ID number(19,0) not null,
        strassenname varchar2(40) not null,
        hausnummer varchar2(4) not null,
        hausnummernZusatz varchar2(6),
        ortsname varchar2(40) not null,
        postleitzahl varchar2(10) not null,
        land varchar2(2),
        asb varchar2(4) not null,
        onkz varchar2(5) not null,
        primary key (id)
    );
    grant select, insert, update on T_MWF_STANDORT_KOLLOKATION to R_HURRICAN_USER;
    grant select on T_MWF_STANDORT_KOLLOKATION to R_HURRICAN_READ_ONLY;

    create table T_MWF_STANDORT_KUNDE (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        MWF_GF_PRODUKT_ID number(19,0) not null,
        anrede varchar2(1) not null,
        nachname varchar2(30) not null,
        vorname varchar2(30),
        strassenname varchar2(40) not null,
        hausnummer varchar2(4) not null,
        hausnummernZusatz varchar2(6),
        ortsname varchar2(40) not null,
        postleitzahl varchar2(10) not null,
        land varchar2(2),
        lageTAEDose varchar2(160),
        primary key (id)
    );
    grant select, insert, update on T_MWF_STANDORT_KUNDE to R_HURRICAN_USER;
    grant select on T_MWF_STANDORT_KUNDE to R_HURRICAN_READ_ONLY;

    alter table T_MWF_KUNDE
        add constraint FK_MWF_KUNDE_2_AUFTRAG
        foreign key (MWF_AUFTRAG_ID)
        references T_MWF_AUFTRAG (ID);

    alter table T_MWF_GESCHAEFTSFALL
        add constraint FK_MWF_GF_2_AUFTRAG
        foreign key (MWF_AUFTRAG_ID)
        references T_MWF_AUFTRAG (ID);

    alter table T_MWF_GF_PRODUKT
        add constraint FK_MWF_PRODUKT_2_AP
        foreign key (MWF_AUFTRAGSPOSITION_ID)
        references T_MWF_AUFTRAGSPOSITION (ID);

    alter table T_MWF_KUNDENWUNSCHTERMIN
        add constraint FK_MWF_KTERMIN_2_GF
        foreign key (MWF_GESCHAEFTSFALL_ID)
        references T_MWF_GESCHAEFTSFALL (ID);

    alter table T_MWF_GF_ANSPRECHPARTNER
        add constraint FK_MWF_ANSP_2_GF
        foreign key (MWF_GESCHAEFTSFALL_ID)
        references T_MWF_GESCHAEFTSFALL (ID);

    alter table T_MWF_GF_ANSPRECHPARTNER
        add constraint FK_GF_ANSP_2_ANSP
        foreign key (MWF_ANSPRECHPARTNER_ID)
        references T_MWF_ANSPRECHPARTNER (ID);

    alter table T_MWF_GF_ANSPRECHPARTNER
        add constraint FK_GF_ANSP_2_AUFTRAGSMGM
        foreign key (MWF_AUFTRAGSMANAGEMENT_ID)
        references T_MWF_AUFTRAGSMANAGEMENT (ID);

    alter table T_MWF_AUFTRAGSPOSITION
        add constraint FK_MWF_AP_2_GF
        foreign key (MWF_GESCHAEFTSFALL_ID)
        references T_MWF_GESCHAEFTSFALL (ID);

    --alter table T_MWF_AUFTRAGSMANAGEMENT
        --add constraint FK_MWF_AM_2_ANSP
        --foreign key (MWF_GF_ANSPRECHPARTNER_ID)
        --references T_MWF_GF_ANSPRECHPARTNER (ID);

    --alter table T_MWF_ANSPRECHPARTNER
        --add constraint FK_MWF_ANSP_2_GFANSP
        --foreign key (MWF_GF_ANSPRECHPARTNER_ID)
        --references T_MWF_GF_ANSPRECHPARTNER (ID);

    alter table T_MWF_STANDORT_KOLLOKATION
        add constraint FK_MWF_STDKOLL_2_GFPRODUKT
        foreign key (MWF_GF_PRODUKT_ID)
        references T_MWF_GF_PRODUKT (ID);

    alter table T_MWF_STANDORT_KUNDE
        add constraint FK_MWF_STDKD_2_GFPROD
        foreign key (MWF_GF_PRODUKT_ID)
        references T_MWF_GF_PRODUKT (ID);

    alter table T_MWF_SCHALTANGABEN
        add constraint FK_MWF_SCHALTANGABEN_2_GFPROD
        foreign key (MWF_GF_PRODUKT_ID)
        references T_MWF_GF_PRODUKT (ID);

    alter table T_MWF_MONTAGELEISTUNG
        add constraint FK_MWF_MONTAGE_2_GFPROD
        foreign key (MWF_GF_PRODUKT_ID)
        references T_MWF_GF_PRODUKT (ID);

    alter table T_MWF_AUFTRAG
        add constraint FK_MWF_AUFTRAG_2_CBV
        foreign key (CB_VORGANG_ID)
        references T_CB_VORGANG(ID);

    create sequence S_T_MWF_ANSPRECHPARTNER_0;
    grant select on S_T_MWF_ANSPRECHPARTNER_0 to public;

    create sequence S_T_MWF_AUFTRAGSMANAGEMENT_0;
    grant select on S_T_MWF_AUFTRAGSMANAGEMENT_0 to public;

    create sequence S_T_MWF_AUFTRAGSPOSITION_0;
    grant select on S_T_MWF_AUFTRAGSPOSITION_0 to public;

    create sequence S_T_MWF_AUFTRAG_0;
    grant select on S_T_MWF_AUFTRAG_0 to public;

    create sequence S_T_MWF_GESCHAEFTSFALL_0;
    grant select on S_T_MWF_GESCHAEFTSFALL_0 to public;

    create sequence S_T_MWF_GF_ANSPRECHPARTNER_0;
    grant select on S_T_MWF_GF_ANSPRECHPARTNER_0 to public;

    create sequence S_T_MWF_GF_PRODUKT_0;
    grant select on S_T_MWF_GF_PRODUKT_0 to public;

    create sequence S_T_MWF_KUNDENWUNSCHTERMIN_0;
    grant select on S_T_MWF_KUNDENWUNSCHTERMIN_0 to public;

    create sequence S_T_MWF_KUNDE_0;
    grant select on S_T_MWF_KUNDE_0 to public;

    create sequence S_T_MWF_MONTAGELEISTUNG_0;
    grant select on S_T_MWF_MONTAGELEISTUNG_0 to public;

    create sequence S_T_MWF_SCHALTANGABEN_0;
    grant select on S_T_MWF_SCHALTANGABEN_0 to public;

    create sequence S_T_MWF_SCHALTUNG_0;
    grant select on S_T_MWF_SCHALTUNG_0 to public;

    create sequence S_T_MWF_SCHALTUNG_KUPFER_0;
    grant select on S_T_MWF_SCHALTUNG_KUPFER_0 to public;

    create sequence S_T_MWF_STANDORT_KOLLOKATION_0;
    grant select on S_T_MWF_STANDORT_KOLLOKATION_0 to public;

    create sequence S_T_MWF_STANDORT_KUNDE_0;
    grant select on S_T_MWF_STANDORT_KUNDE_0 to public;
