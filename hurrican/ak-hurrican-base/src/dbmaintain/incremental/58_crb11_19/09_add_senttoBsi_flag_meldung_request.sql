
-- Sent Flag an Meldung/Request anfuegen default auf 0 = nicht gesendet
ALTER TABLE T_MWF_MELDUNG ADD (SENT_TO_BSI number(1) default 0);

ALTER TABLE T_MWF_REQUEST ADD (SENT_TO_BSI number(1) default 0);
