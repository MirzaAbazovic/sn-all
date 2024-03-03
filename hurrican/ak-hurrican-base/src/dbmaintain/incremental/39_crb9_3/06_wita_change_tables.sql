    drop table T_MWF_ANSPRECHPARTNER cascade constraints;
    drop table T_MWF_AUFTRAG cascade constraints;
    drop table T_MWF_AUFTRAGSMANAGEMENT cascade constraints;
    drop table T_MWF_AUFTRAGSPOSITION cascade constraints;
    drop table T_MWF_GESCHAEFTSFALL cascade constraints;
    drop table T_MWF_GF_ANSPRECHPARTNER cascade constraints;
    drop table T_MWF_GF_PRODUKT cascade constraints;
    drop table T_MWF_KUNDE cascade constraints;
    drop table T_MWF_KUNDENWUNSCHTERMIN cascade constraints;
    drop table T_MWF_MONTAGELEISTUNG cascade constraints;
    drop table T_MWF_SCHALTANGABEN cascade constraints;
    drop table T_MWF_SCHALTUNG_KUPFER cascade constraints;
    drop table T_MWF_STANDORT_KOLLOKATION cascade constraints;
    drop table T_MWF_STANDORT_KUNDE cascade constraints;


    create table T_MWF_ANSPRECHPARTNER (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        anrede varchar2(10) not null,
        nachname varchar2(30) not null,
        vorname varchar2(30),
        email varchar2(320),
        telefonnummer varchar2(30),
        rolle varchar2(255 char),
        primary key (id)
    );
    grant select, insert, update on  T_MWF_ANSPRECHPARTNER to R_HURRICAN_USER;
    grant select on  T_MWF_ANSPRECHPARTNER to R_HURRICAN_READ_ONLY;

    create table T_MWF_AUFTRAG (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        CB_VORGANG_ID number(10,0) not null,
        geschaeftsfall_id number(19,0) not null,
        kunde_id number(19,0) not null,
        primary key (id)
    );
    grant select, insert, update on  T_MWF_AUFTRAG to R_HURRICAN_USER;
    grant select on  T_MWF_AUFTRAG to R_HURRICAN_READ_ONLY;

    create table T_MWF_AUFTRAGSMANAGEMENT (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        anrede varchar2(10) not null,
        nachname varchar2(30) not null,
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
        produkt varchar2(100) not null,
        produktBezeichner varchar2(10) not null,
        geschaeftsfallProdukt_id number(19,0) not null,
        primary key (id)
    );
    grant select, insert, update on  T_MWF_AUFTRAGSPOSITION to R_HURRICAN_USER;
    grant select on  T_MWF_AUFTRAGSPOSITION to R_HURRICAN_READ_ONLY;

    create table T_MWF_GESCHAEFTSFALL (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        geschaeftsfalltyp varchar2(30) not null,
        auftragsPosition_id number(19,0) not null,
        gfAnsprechpartner_id number(19,0) not null,
        kundenwunschtermin_id number(19,0) not null,
        primary key (id)
    );
    grant select, insert, update on  T_MWF_GESCHAEFTSFALL to R_HURRICAN_USER;
    grant select on  T_MWF_GESCHAEFTSFALL to R_HURRICAN_READ_ONLY;

    create table T_MWF_GF_ANSPRECHPARTNER (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        ansprechpartner_id number(19,0),
        auftragsmanagement_id number(19,0) not null,
        primary key (id)
    );
    grant select, insert, update on  T_MWF_GF_ANSPRECHPARTNER to R_HURRICAN_USER;
    grant select on  T_MWF_GF_ANSPRECHPARTNER to R_HURRICAN_READ_ONLY;

    create table T_MWF_GF_PRODUKT (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        standortA_id number(19,0) not null,
        standortB_id number(19,0) not null,
        montageleistung_id number(19,0),
        schaltangaben_id number(19,0),
        primary key (id)
    );
    grant select, insert, update on  T_MWF_GF_PRODUKT to R_HURRICAN_USER;
    grant select on  T_MWF_GF_PRODUKT to R_HURRICAN_READ_ONLY;

    create table T_MWF_KUNDE (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        kundennummer varchar2(10) not null,
        leistungsnummer varchar2(10) not null,
        primary key (id)
    );
    grant select, insert, update on  T_MWF_KUNDE to R_HURRICAN_USER;
    grant select on  T_MWF_KUNDE to R_HURRICAN_READ_ONLY;

    create table T_MWF_KUNDENWUNSCHTERMIN (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        datum date not null,
        zeitfenster varchar2(6),
        primary key (id)
    );
    grant select, insert, update on  T_MWF_KUNDENWUNSCHTERMIN to R_HURRICAN_USER;
    grant select on  T_MWF_KUNDENWUNSCHTERMIN to R_HURRICAN_READ_ONLY;

    create table T_MWF_MONTAGELEISTUNG (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        anrede varchar2(10) not null,
        nachname varchar2(30) not null,
        vorname varchar2(30),
        telefonnummer varchar2(30),
        emailadresse varchar2(320),
        montagehinweis varchar2(160),
        primary key (id)
    );
    grant select, insert, update on  T_MWF_MONTAGELEISTUNG to R_HURRICAN_USER;
    grant select on  T_MWF_MONTAGELEISTUNG to R_HURRICAN_READ_ONLY;

    create table T_MWF_SCHALTANGABEN (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        primary key (id)
    );
    grant select, insert, update on  T_MWF_SCHALTANGABEN to R_HURRICAN_USER;
    grant select on  T_MWF_SCHALTANGABEN to R_HURRICAN_READ_ONLY;

    create table T_MWF_SCHALTUNG_KUPFER (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        SCHALTANGABEN_ID number(19,0) not null,
        uebertragungsverfahren varchar2(3) not null,
        UEVT varchar2(4) not null,
        EVS varchar2(2) not null,
        doppelader varchar2(2) not null,
        primary key (id)
    );
    grant select, insert, update on  T_MWF_SCHALTUNG_KUPFER to R_HURRICAN_USER;
    grant select on  T_MWF_SCHALTUNG_KUPFER to R_HURRICAN_READ_ONLY;

    create table T_MWF_STANDORT_KOLLOKATION (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        strassenname varchar2(40) not null,
        hausnummer varchar2(4) not null,
        hausnummernZusatz varchar2(6),
        ortsname varchar2(40) not null,
        postleitzahl varchar2(10) not null,
        onkz varchar2(5) not null,
        asb varchar2(4) not null,
        land varchar2(2),
        primary key (id)
    );
    grant select, insert, update on  T_MWF_STANDORT_KOLLOKATION to R_HURRICAN_USER;
    grant select on  T_MWF_STANDORT_KOLLOKATION to R_HURRICAN_READ_ONLY;

    create table T_MWF_STANDORT_KUNDE (
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
        lageTAEDose varchar2(160),
        primary key (id)
    );
    grant select, insert, update on  T_MWF_STANDORT_KUNDE to R_HURRICAN_USER;
    grant select on  T_MWF_STANDORT_KUNDE to R_HURRICAN_READ_ONLY;

    create table T_MWF_SCHALTUNG_KVZ (
	    id number(19,0) not null,
	    version number(19,0) default 0 not null,
	    SCHALTANGABEN_ID number(19,0) not null,
	    kvz varchar2(8) not null,
	    kvzschaltnummer varchar2(8) not null,
	    primary key (id)
	);
	grant select, insert, update on  T_MWF_SCHALTUNG_KVZ to R_HURRICAN_USER;
    grant select on  T_MWF_SCHALTUNG_KVZ to R_HURRICAN_READ_ONLY;

    alter table T_MWF_AUFTRAG
        add constraint FK67287E8B9848977
        foreign key (geschaeftsfall_id)
        references T_MWF_GESCHAEFTSFALL;

    alter table T_MWF_AUFTRAG
        add constraint FK67287E8826C5FFD
        foreign key (kunde_id)
        references T_MWF_KUNDE;

    alter table T_MWF_AUFTRAGSPOSITION
        add constraint FKA46C6394C31715DD
        foreign key (geschaeftsfallProdukt_id)
        references T_MWF_GF_PRODUKT;

    alter table T_MWF_GESCHAEFTSFALL
        add constraint FKAC3D0D10D51E7EF7
        foreign key (kundenwunschtermin_id)
        references T_MWF_KUNDENWUNSCHTERMIN;

    alter table T_MWF_GESCHAEFTSFALL
        add constraint FKAC3D0D10B5CF1E7D
        foreign key (gfAnsprechpartner_id)
        references T_MWF_GF_ANSPRECHPARTNER;

    alter table T_MWF_GESCHAEFTSFALL
        add constraint FKAC3D0D10DF54CFB7
        foreign key (auftragsPosition_id)
        references T_MWF_AUFTRAGSPOSITION;

    alter table T_MWF_GF_ANSPRECHPARTNER
        add constraint FKCFBEFC74AF5F09D
        foreign key (ansprechpartner_id)
        references T_MWF_ANSPRECHPARTNER;

    alter table T_MWF_GF_ANSPRECHPARTNER
        add constraint FKCFBEFC743E740E37
        foreign key (auftragsmanagement_id)
        references T_MWF_AUFTRAGSMANAGEMENT;

    alter table T_MWF_GF_PRODUKT
        add constraint FKA5F55DF5172E21
        foreign key (standortA_id)
        references T_MWF_STANDORT_KUNDE;

    alter table T_MWF_GF_PRODUKT
        add constraint FKA5F55DF57C8ADE3D
        foreign key (schaltangaben_id)
        references T_MWF_SCHALTANGABEN;

    alter table T_MWF_GF_PRODUKT
        add constraint FKA5F55DF5D7DFBCF0
        foreign key (standortB_id)
        references T_MWF_STANDORT_KOLLOKATION;

    alter table T_MWF_GF_PRODUKT
        add constraint FKA5F55DF58D343DDD
        foreign key (montageleistung_id)
        references T_MWF_MONTAGELEISTUNG;

	alter table T_MWF_SCHALTUNG_KVZ
	    add constraint FK2FC79F5F7C8ADE3D
	    foreign key (SCHALTANGABEN_ID)
	    references T_MWF_SCHALTANGABEN;

	create sequence S_T_MWF_SCHALTUNG_KVZ_0;
	grant select on S_T_MWF_SCHALTUNG_KVZ_0 to public;
