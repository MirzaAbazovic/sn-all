-- column used for determining the earliest send date for a WitaRequest.
-- When set to null or a date in past then the request can be sent.
ALTER TABLE T_MWF_REQUEST ADD EARLIEST_SEND_DATE DATE;