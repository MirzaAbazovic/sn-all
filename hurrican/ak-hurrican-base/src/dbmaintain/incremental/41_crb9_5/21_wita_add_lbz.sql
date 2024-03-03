alter table T_MWF_GF_PRODUKT add LEITUNGSBEZEICHNUNG_ID NUMBER(19,0);

    alter table T_MWF_GF_PRODUKT
        add constraint FK_MWFGFP_2_LBZ
        foreign key (LEITUNGSBEZEICHNUNG_ID)
        references T_MWF_LEITUNGS_BEZEICHNUNG;
