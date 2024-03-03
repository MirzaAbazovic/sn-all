-- Migration script to fix the T_WBCI_MELDUNG.TYP 
-- The Storno Erledigt Meldungen classes used the wrong discriminator value. This script  
-- migrates the TYP to the correct value

update T_WBCI_MELDUNG set TYP='ERLM-STRAUF' where ID in (
  select
    m.ID
  from
    T_WBCI_GESCHAEFTSFALL gf
    JOIN T_WBCI_MELDUNG m ON (m.GESCHAEFTSFALL_ID = gf.ID)
    JOIN T_WBCI_REQUEST r ON (r.GESCHAEFTSFALL_ID = gf.ID)
  where
    r.TYP like 'STR-AUFH-%'
    AND m.TYP like 'ERLM-STRAEN'
    AND m.STORNO_ID_REF = r.AENDERUNGS_ID
);

update T_WBCI_MELDUNG set TYP='ERLM-STRAEN' where ID in (
  select
    m.ID
  from
    T_WBCI_GESCHAEFTSFALL gf
    JOIN T_WBCI_MELDUNG m ON (m.GESCHAEFTSFALL_ID = gf.ID)
    JOIN T_WBCI_REQUEST r ON (r.GESCHAEFTSFALL_ID = gf.ID)
  where
    r.TYP like 'STR-AEN-%'
    AND m.TYP like 'ERLM-STRAUF'
    AND m.STORNO_ID_REF = r.AENDERUNGS_ID
);
