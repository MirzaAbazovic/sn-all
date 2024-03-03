
	create table T_MWF_QEB (
        id number(19,0) not null,
        version number(19,0) default 0 not null,
        KUNDEN_NUMMER varchar2(10) not null,
        ext_auftrags_nr varchar2(20) not null,
        vertrags_nummer varchar2(10),
        primary key (id)
    );
    grant select, insert, update on  T_MWF_QEB to R_HURRICAN_USER;
    grant select on  T_MWF_QEB to R_HURRICAN_READ_ONLY;

    create sequence S_T_MWF_QEB_0 start with 1;
    grant select on S_T_MWF_QEB_0 to public;
    
    