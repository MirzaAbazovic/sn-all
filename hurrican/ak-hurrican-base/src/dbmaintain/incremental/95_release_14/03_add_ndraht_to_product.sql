alter table T_PRODUKT add SDSL_N_DRAHT varchar2(12);
alter table T_PRODUKT add CONSTRAINT T_PRODUKT_SDSL_N_DRAHT check (SDSL_N_DRAHT IN ('ZWEI', 'VIER', 'ACHT'));
