--
-- Add a field called BAD_DATA to MIG_MIGRATIONRESULT
--

ALTER TABLE MIG_MIGRATIONRESULT
 ADD (BAD_DATA  NUMBER(10));

