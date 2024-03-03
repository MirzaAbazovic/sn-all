create table T_CARRIERBESTELLUNG_PV (
    ID NUMBER(19,0) not null
  , VERSION NUMBER(19,0) not null
  , CARRIER_ID NUMBER(19,0) not null
  , PRODUKT_GRUPPE VARCHAR2(10) not null
  , PROVIDER_VTRNR VARCHAR2(10)
  , PROVIDER_LBZ VARCHAR2(35)
  , CONSTRAINT C_CBPV_PRODUKT CHECK (PRODUKT_GRUPPE in ('TAL', 'SONSTIGES'))
  , PRIMARY KEY (ID)
);


grant select, insert, update on  T_CARRIERBESTELLUNG_PV to R_HURRICAN_USER;
grant select on  T_CARRIERBESTELLUNG_PV to R_HURRICAN_READ_ONLY;

create sequence S_T_CARRIERBESTELLUNG_PV_0 start with 1;
grant select on S_T_CARRIERBESTELLUNG_PV_0 to public;

alter table T_CARRIERBESTELLUNG add CARRIERBESTELLUNGPV_ID NUMBER(19,0);
alter table T_CARRIERBESTELLUNG
    add constraint FK_CB_2_CBPV
    foreign key (CARRIERBESTELLUNGPV_ID)
    references T_CARRIERBESTELLUNG_PV (ID);

alter table T_CARRIERBESTELLUNG_PV
    add constraint FK_CBPV_2_CARRIER
    foreign key (CARRIER_ID)
    references T_CARRIER (ID);
