-- Remove Zeitfenster from T_MWF_REQUEST table
ALTER TABLE T_MWF_REQUEST DROP CONSTRAINT T_MWF_REQUEST_TIME_SLOT;
ALTER TABLE T_MWF_REQUEST DROP COLUMN TIME_SLOT;