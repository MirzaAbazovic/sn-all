-- Standorte mit allen virtuellen Subracks und den Korrekturoffsets
--
-- Zur Prüfung, ob alle FttC_KVZ Standorte erfasst sind, folgendes Statement verwenden:
--    select g.ORTSTEIL from T_HVT_GRUPPE g, T_HVT_STANDORT s, T_HW_RACK r, T_HW_SUBRACK sr
--    where g.HVT_GRUPPE_ID = s.HVT_GRUPPE_ID
--      and s.STANDORT_TYP_REF_ID = 11013
--      and s.HVT_ID_STANDORT = r.HVT_ID_STANDORT
--      and r.ID = sr.RACK_ID
--      and sr.ID not in (select b.SUBRACK_ID from T_HW_BAUGRUPPE b where b.RACK_ID=sr.RACK_ID)
--      group by g.ORTSTEIL;
-- Das Statement geht von der Annahme aus, dass Subracks ohne Baugruppen virtuelle Subracks sind.
-- Das Resultat des Statements weicht mit Martin Leichter's Angaben in HUR-3474 nicht ab.
--
-- Hier die Liste mit den per Hand berechneten Korrekturoffsets:
-- ERL-HEUST-047-MFG Subrack 1-1, Subracktyp NFXS-B -> 8*48=384
-- ERL-KIESE-059-MFG Subrack 1-1, Subracktyp NFXS-B -> 8*48=384
-- ERL-KIESE-003-MFG Subrack 1-1, Subracktyp NFXS-B und Subrack 2-1, Subracktyp NFXR-A -> (8*48)+(2*48)=480
-- ERL-NIEDE-066-MFG Subrack 1-1, Subracktyp NFXS-B -> 8*48=384
-- ERL-ELIAU-006b-MFG Subrack 1-1, Subracktyp NFXS-B -> 8*48=384
-- ERL-BRUCK-008-MFG Subrack 1-1, Subracktyp NFXS-B -> 8*48=384
-- KEN-DIESE-000-MFG Subrack 1-1, Subracktyp NFXS-B und Subrack 2-1, Subracktyp NFXR-A -> (8*48)+(2*48)=480
-- KEN-DIESE-018-MFG Subrack 1-1, Subracktyp NFXS-B -> 8*48=384

update T_HW_RACK_DSLAM set CC_OFFSET='384' where RACK_ID in (select r.ID from T_HVT_GRUPPE g, T_HVT_STANDORT s, T_HW_RACK r
  where g.HVT_GRUPPE_ID = s.HVT_GRUPPE_ID and r.HVT_ID_STANDORT=s.HVT_ID_STANDORT
  and g.ORTSTEIL = 'ERL-HEUST-047-MFG');

update T_HW_RACK_DSLAM set CC_OFFSET='384' where RACK_ID in (select r.ID from T_HVT_GRUPPE g, T_HVT_STANDORT s, T_HW_RACK r
  where g.HVT_GRUPPE_ID = s.HVT_GRUPPE_ID and r.HVT_ID_STANDORT=s.HVT_ID_STANDORT
  and g.ORTSTEIL = 'ERL-KIESE-059-MFG');

update T_HW_RACK_DSLAM set CC_OFFSET='480' where RACK_ID in (select r.ID from T_HVT_GRUPPE g, T_HVT_STANDORT s, T_HW_RACK r
  where g.HVT_GRUPPE_ID = s.HVT_GRUPPE_ID and r.HVT_ID_STANDORT=s.HVT_ID_STANDORT
  and g.ORTSTEIL = 'ERL-KIESE-003-MFG');

update T_HW_RACK_DSLAM set CC_OFFSET='384' where RACK_ID in (select r.ID from T_HVT_GRUPPE g, T_HVT_STANDORT s, T_HW_RACK r
  where g.HVT_GRUPPE_ID = s.HVT_GRUPPE_ID and r.HVT_ID_STANDORT=s.HVT_ID_STANDORT
  and g.ORTSTEIL = 'ERL-NIEDE-066-MFG');

update T_HW_RACK_DSLAM set CC_OFFSET='384' where RACK_ID in (select r.ID from T_HVT_GRUPPE g, T_HVT_STANDORT s, T_HW_RACK r
  where g.HVT_GRUPPE_ID = s.HVT_GRUPPE_ID and r.HVT_ID_STANDORT=s.HVT_ID_STANDORT
  and g.ORTSTEIL = 'ERL-ELIAU-006b-MFG');

update T_HW_RACK_DSLAM set CC_OFFSET='384' where RACK_ID in (select r.ID from T_HVT_GRUPPE g, T_HVT_STANDORT s, T_HW_RACK r
  where g.HVT_GRUPPE_ID = s.HVT_GRUPPE_ID and r.HVT_ID_STANDORT=s.HVT_ID_STANDORT
  and g.ORTSTEIL = 'ERL-BRUCK-008-MFG');

update T_HW_RACK_DSLAM set CC_OFFSET='480' where RACK_ID in (select r.ID from T_HVT_GRUPPE g, T_HVT_STANDORT s, T_HW_RACK r
  where g.HVT_GRUPPE_ID = s.HVT_GRUPPE_ID and r.HVT_ID_STANDORT=s.HVT_ID_STANDORT
  and g.ORTSTEIL = 'KEN-DIESE-000-MFG');

update T_HW_RACK_DSLAM set CC_OFFSET='384' where RACK_ID in (select r.ID from T_HVT_GRUPPE g, T_HVT_STANDORT s, T_HW_RACK r
  where g.HVT_GRUPPE_ID = s.HVT_GRUPPE_ID and r.HVT_ID_STANDORT=s.HVT_ID_STANDORT
  and g.ORTSTEIL = 'KEN-DIESE-018-MFG');
