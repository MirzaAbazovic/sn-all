--
-- SQL-Script, um eine View fuer die CPS-Initialisierung
-- anzulegen.
-- Die View ermittelt alle aktiven Auftraege, die noch keine
-- CPS-Transaction besitzen.
-- Es werden nur Produkte beruecksichtigt, die eine
-- CPS-Konfiguration (CPS-Produktnamen) besitzen.
--

CREATE or REPLACE FORCE VIEW V_MIG_CPS_INIT as
  select
      distinct
        a.ID as AUFTRAG_ID
      , ad.PRODAK_ORDER__NO
      , a.KUNDE__NO
      , ad.STATUS_ID
      , cpstx.ID as CPS_TX_ID
      , hvt.HVT_ID_STANDORT as HVT_ID_STANDORT
      , ATECH.NIEDERLASSUNG_ID as NIEDERLASSUNG_ID
  from
    T_AUFTRAG a
    inner join T_AUFTRAG_DATEN ad on a.ID=ad.AUFTRAG_ID
    inner join T_AUFTRAG_TECHNIK atech on a.ID=atech.AUFTRAG_ID
    inner join T_PRODUKT prod on ad.PROD_ID=prod.PROD_ID
    left join T_CPS_TX cpstx on ad.PRODAK_ORDER__NO=cpstx.TAIFUN_ORDER__NO
    left join T_VERLAUF verlauf on a.ID=verlauf.AUFTRAG_ID
    left join T_ENDSTELLE e on atech.AT_2_ES_ID=e.ES_GRUPPE
    left join T_HVT_STANDORT hvt on e.HVT_ID_STANDORT=hvt.HVT_ID_STANDORT
    left join T_HVT_GRUPPE hvtg on hvt.HVT_GRUPPE_ID=hvtg.HVT_GRUPPE_ID
    left join T_TDN tdn on atech.TDN_ID=tdn.ID
    left join T_RANGIERUNG rang on e.RANGIER_ID=rang.RANGIER_ID     -- fuer Port-Schwenk
    left join T_EQUIPMENT eq on rang.EQ_IN_ID=eq.EQ_ID              -- fuer Port-Schwenk
    left join T_HW_BAUGRUPPE hwbg on EQ.HW_BAUGRUPPEN_ID=HWBG.ID    -- fuer Port-Schwenk
    left join T_HW_RACK rack on HWBG.RACK_ID=RACK.ID                -- fuer Port-Schwenk
  where
    ad.GUELTIG_BIS>SYSDATE
    and atech.GUELTIG_BIS>SYSDATE
    and
    (
      (
        ad.STATUS_ID >= 6000 and ad.STATUS_ID <= 9100  -- Auftraege in Betrieb oder noch nicht gekuendigt
      )
      or
      (
        ad.STATUS_ID > 9100 and ad.STATUS_ID<=9800  -- Auftraege im Kuendiungsverlauf
        and
          (
            verlauf.AKT='1' and verlauf.REALISIERUNGSTERMIN>SYSDATE  -- nur Init, wenn Realisierungstermin noch nicht ueberschritten
          )
      )
      or
      (
        ad.STATUS_ID >= 4000 and ad.STATUS_ID < 6000  -- Auftraege in Realisierung und Real-Date ueberschritten
        and verlauf.REALISIERUNGSTERMIN <= SYSDATE
        and verlauf.AKT='1'
        and verlauf.PROJEKTIERUNG<>'1'
      )
    )
    and cpstx.ID is null
    and prod.CPS_PROD_NAME is not null
    and ad.PRODAK_ORDER__NO is not null
    and (atech.PREVENT_CPS_PROV<>'1' or atech.PREVENT_CPS_PROV is null)
    and (e.ES_TYP='B' or e.ES_TYP is null)
    and E.HVT_ID_STANDORT is not null
    and (tdn.ID is null or tdn.TDN not like 'lhm%')
    and RACK.MANAGEMENTBEZ='IMPLE018-01'
    and RANG.GUELTIG_BIS>SYSDATE                        -- fuer Port-Schwenk
    --and (hvtg.NIEDERLASSUNG_ID=3 or hvt.HVT_ID_STANDORT in (25,26,27,28,29,31,32,33,35,36))
  order by a.ID ASC
;




