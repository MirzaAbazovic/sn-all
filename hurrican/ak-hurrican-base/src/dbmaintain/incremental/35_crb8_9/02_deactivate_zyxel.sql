
update T_HVT_STANDORT set
    HOCHGERECHNETER_TERMIN='nicht in Betrieb',
    GESICHERTE_REALISIERUNG='nicht in Betrieb',
    gueltig_bis=to_date('01.03.2011', 'dd.mm.yyyy')
  where HVT_ID_STANDORT in (
    select STD.HVT_ID_STANDORT
     from
       T_HVT_STANDORT std
       inner join T_HVT_STANDORT_2_TECHNIK s2t on STD.HVT_ID_STANDORT=S2T.HVT_ID_STANDORT
       inner join T_HVT_GRUPPE g on STD.HVT_GRUPPE_ID=G.HVT_GRUPPE_ID
     where S2T.HVT_TECHNIK_ID=4
);
