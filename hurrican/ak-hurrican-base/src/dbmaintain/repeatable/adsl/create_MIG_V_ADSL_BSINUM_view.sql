--
-- View worüber die BSI-Nummern migriert werden
--
CREATE OR REPLACE FORCE VIEW MIG_V_ADSL_BSINUM AS
SELECT   am.*,
         COUNT ( * ) OVER (PARTITION BY AM.AUFTRAG_ID_KUP)
            AS NUM_HURRICAN_AUF
  FROM   MIG_AUFTRAG_MAPPING am
/
