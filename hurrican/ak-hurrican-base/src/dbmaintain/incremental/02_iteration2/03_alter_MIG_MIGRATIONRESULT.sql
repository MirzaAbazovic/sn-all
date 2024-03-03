--
-- Andere Success auf String, um darin Enums speichern zu können.
--

DELETE FROM MIG_MIGRATIONRESULT;

ALTER TABLE MIG_MIGRATIONRESULT
MODIFY(SUCCESS VARCHAR2(50 BYTE));

commit;
