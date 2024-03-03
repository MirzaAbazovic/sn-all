update T_WBCI_MELDUNG set TECHNOLOGIE='TAL_ISDN' where TECHNOLOGIE='001 TAL ISDN';
update T_WBCI_MELDUNG set TECHNOLOGIE='TAL_DSL' where TECHNOLOGIE='002 TAL DSL';
update T_WBCI_MELDUNG set TECHNOLOGIE='TAL_VDSL' where TECHNOLOGIE='003 TAL VDSL';
update T_WBCI_MELDUNG set TECHNOLOGIE='ADSL_SA_Annex_J' where TECHNOLOGIE='004 ADSL SA Annex J';
update T_WBCI_MELDUNG set TECHNOLOGIE='ADSL_SA_Annex_B' where TECHNOLOGIE='005 ADSL SA Annex B';
update T_WBCI_MELDUNG set TECHNOLOGIE='ADSL_SA' where TECHNOLOGIE='006 ADSL SA';
update T_WBCI_MELDUNG set TECHNOLOGIE='ADSL_SH' where TECHNOLOGIE='007 ADSL SH';
update T_WBCI_MELDUNG set TECHNOLOGIE='SDSL_SA' where TECHNOLOGIE='008 SDSL SA';
update T_WBCI_MELDUNG set TECHNOLOGIE='VDSL_SA' where TECHNOLOGIE='009 VDSL SA';
update T_WBCI_MELDUNG set TECHNOLOGIE='UMTS' where TECHNOLOGIE='010 UMTS';
update T_WBCI_MELDUNG set TECHNOLOGIE='LTE' where TECHNOLOGIE='011 LTE';
update T_WBCI_MELDUNG set TECHNOLOGIE='FTTC' where TECHNOLOGIE='012 FTTC';
update T_WBCI_MELDUNG set TECHNOLOGIE='FTTB' where TECHNOLOGIE='013 FTTB';
update T_WBCI_MELDUNG set TECHNOLOGIE='FTTH' where TECHNOLOGIE='014 FTTH';
update T_WBCI_MELDUNG set TECHNOLOGIE='HFC' where TECHNOLOGIE='015 HFC';
update T_WBCI_MELDUNG set TECHNOLOGIE='KOAX' where TECHNOLOGIE='016 Koax';
update T_WBCI_MELDUNG set TECHNOLOGIE='KUPFER' where TECHNOLOGIE='017 Kupfer';
update T_WBCI_MELDUNG set TECHNOLOGIE='KUPFER_MX' where TECHNOLOGIE='018 Kupfer MX';
update T_WBCI_MELDUNG set TECHNOLOGIE='GF' where TECHNOLOGIE='019 GF';
update T_WBCI_MELDUNG set TECHNOLOGIE='KUPFER_GF' where TECHNOLOGIE='020 Kupfer GF';
update T_WBCI_MELDUNG set TECHNOLOGIE='SONSTIGES' where TECHNOLOGIE='021 Sonstiges';

update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='TAL_ISDN' where MNET_TECHNOLOGIE='001 TAL ISDN';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='TAL_DSL' where MNET_TECHNOLOGIE='002 TAL DSL';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='TAL_VDSL' where MNET_TECHNOLOGIE='003 TAL VDSL';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='ADSL_SA_Annex_J' where MNET_TECHNOLOGIE='004 ADSL SA Annex J';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='ADSL_SA_Annex_B' where MNET_TECHNOLOGIE='005 ADSL SA Annex B';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='ADSL_SA' where MNET_TECHNOLOGIE='006 ADSL SA';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='ADSL_SH' where MNET_TECHNOLOGIE='007 ADSL SH';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='SDSL_SA' where MNET_TECHNOLOGIE='008 SDSL SA';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='VDSL_SA' where MNET_TECHNOLOGIE='009 VDSL SA';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='UMTS' where MNET_TECHNOLOGIE='010 UMTS';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='LTE' where MNET_TECHNOLOGIE='011 LTE';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='FTTC' where MNET_TECHNOLOGIE='012 FTTC';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='FTTB' where MNET_TECHNOLOGIE='013 FTTB';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='FTTH' where MNET_TECHNOLOGIE='014 FTTH';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='HFC' where MNET_TECHNOLOGIE='015 HFC';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='KOAX' where MNET_TECHNOLOGIE='016 Koax';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='KUPFER' where MNET_TECHNOLOGIE='017 Kupfer';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='KUPFER_MX' where MNET_TECHNOLOGIE='018 Kupfer MX';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='GF' where MNET_TECHNOLOGIE='019 GF';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='KUPFER_GF' where MNET_TECHNOLOGIE='020 Kupfer GF';
update T_WBCI_GESCHAEFTSFALL set MNET_TECHNOLOGIE='SONSTIGES' where MNET_TECHNOLOGIE='021 Sonstiges';


ALTER TABLE T_WBCI_MELDUNG
ADD CONSTRAINT CK_WBCIMELDUNG_TECHNOLOGIE
CHECK (TECHNOLOGIE IN (
    'TAL_ISDN',
    'TAL_DSL',
    'TAL_VDSL',
    'ADSL_SA_Annex_J',
    'ADSL_SA_Annex_B',
    'ADSL_SA',
    'ADSL_SH',
    'SDSL_SA',
    'VDSL_SA',
    'UMTS',
    'LTE',
    'FTTC',
    'FTTB',
    'FTTH',
    'HFC',
    'KOAX',
    'KUPFER',
    'KUPFER_MX',
    'GF',
    'KUPFER_GF',
    'SONSTIGES'
  )) ENABLE NOVALIDATE;

ALTER TABLE T_WBCI_GESCHAEFTSFALL
ADD CONSTRAINT CK_WBCIGF_TECHNOLOGIE
CHECK (MNET_TECHNOLOGIE IN (
    'TAL_ISDN',
    'TAL_DSL',
    'TAL_VDSL',
    'ADSL_SA_Annex_J',
    'ADSL_SA_Annex_B',
    'ADSL_SA',
    'ADSL_SH',
    'SDSL_SA',
    'VDSL_SA',
    'UMTS',
    'LTE',
    'FTTC',
    'FTTB',
    'FTTH',
    'HFC',
    'KOAX',
    'KUPFER',
    'KUPFER_MX',
    'GF',
    'KUPFER_GF',
    'SONSTIGES'
  )) ENABLE NOVALIDATE;