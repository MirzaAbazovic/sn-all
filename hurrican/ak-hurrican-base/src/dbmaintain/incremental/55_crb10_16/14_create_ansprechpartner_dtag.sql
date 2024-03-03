CREATE TABLE T_MWF_ANSPRECHPARTNER_DTAG(
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        anrede varchar2(10),
        nachname varchar2(30),
        vorname varchar2(30),
        telefonnummer varchar2(30),
        emailadresse varchar2(320),
        primary key (id)
    );
grant select, insert, update on  T_MWF_ANSPRECHPARTNER_DTAG to R_HURRICAN_USER;
grant select on T_MWF_ANSPRECHPARTNER_DTAG to R_HURRICAN_READ_ONLY;

create sequence S_T_MWF_ANSPRECHPARTNER_DTAG_0;
grant select on S_T_MWF_ANSPRECHPARTNER_DTAG_0 to public;

alter table T_MWF_MELDUNGS_POSITION ADD (ANSPRECHPARTNERTELEKOM_ID NUMBER(19));
alter table T_MWF_MELDUNGS_POSITION add constraint FK_MPOS2ANSPR_DTAG foreign key (ANSPRECHPARTNERTELEKOM_ID) references T_MWF_ANSPRECHPARTNER_DTAG;
