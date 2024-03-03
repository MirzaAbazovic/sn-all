--
-- Add a reference to the Migration which started the Transformation
--

ALTER TABLE MIG_LOG
 ADD (MIGRESULT_ID  NUMBER(10));

ALTER TABLE MIG_LOG
 ADD FOREIGN KEY (MIGRESULT_ID)
 REFERENCES MIG_MIGRATIONRESULT (ID);
