-- Skript, um eine View mit Baugruppen-Informationen zu generieren.

create or replace force view V_HW_BAUGRUPPEN as (
select
    RACK.HVT_ID_STANDORT,
    rack.ID as RACK_ID,
    RACK.ANLAGENBEZ,
    RACK.GERAETEBEZ,
    RACK.RACK_TYP,
    BGTYP.NAME,
    HERSTELLER.HERSTELLER,
    SUBRACK.MOD_NUMBER as SUBRACK_MOD_NUMBER,
    SUBRACKTYP.NAME as SUBRACK_TYP,
    BG.MOD_NUMBER as BG_MOD_NUMBER,
    BG.EINGEBAUT,
    BG.ID as HW_BAUGRUPPEN_ID,
    BGTYP.NAME as HW_BAUGRUPPEN_TYP
  from T_HW_RACK rack
    inner join T_HW_BAUGRUPPE bg on RACK.ID=BG.RACK_ID
    inner join T_HW_BAUGRUPPEN_TYP bgtyp on BG.HW_BG_TYP_ID=BGTYP.ID
    left join T_HW_SUBRACK subrack on BG.SUBRACK_ID=subrack.ID
    left join T_HW_SUBRACK_TYP subracktyp on SUBRACK.SUBRACK_TYP_ID=subracktyp.ID
    inner join T_HVT_TECHNIK hersteller on RACK.HW_PRODUCER=hersteller.ID
  where RACK.GUELTIG_BIS>SYSDATE
);

grant select on V_HW_BAUGRUPPEN to R_HURRICAN_USER;
grant select on V_HW_BAUGRUPPEN to R_HURRICAN_READ_ONLY;

