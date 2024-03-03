alter table T_MWF_GF_PRODUKT modify (STANDORTA_ID null);
alter table T_MWF_GF_PRODUKT modify (STANDORTB_ID null);
alter table T_MWF_GESCHAEFTSFALL add VERTRAGSNUMMER VARCHAR2(10);