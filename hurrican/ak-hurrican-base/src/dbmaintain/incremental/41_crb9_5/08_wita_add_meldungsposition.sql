    create table T_MWF_MELDUNGS_POSITION (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        ABM_MELDUNG_ID number(19,0),
        ABBM_MELDUNG_ID number(19,0),
        MELDUNGS_CODE varchar2(10) not null,
        MELDUNGS_TEXT varchar2(255) not null,
        primary key (id)
    );
    grant select, insert, update on  T_MWF_MELDUNGS_POSITION to R_HURRICAN_USER;
    grant select on  T_MWF_MELDUNGS_POSITION to R_HURRICAN_READ_ONLY;
    create sequence S_T_MWF_MELDUNGS_POSITION_0 start with 1;
    grant select on S_T_MWF_MELDUNGS_POSITION_0 to public;


    alter table T_MWF_MELDUNGS_POSITION
        add constraint FK_MWFMP_2_ABBM
        foreign key (ABBM_MELDUNG_ID)
        references T_MWF_ABBM;

    alter table T_MWF_MELDUNGS_POSITION
        add constraint FK_MWFMP_2_ABM
        foreign key (ABM_MELDUNG_ID)
        references T_MWF_ABM;
