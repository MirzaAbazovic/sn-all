-- Trigger setzt den SMS_STATUS und/oder EMAIL_STATUS bei neuen Meldungen auf 'OFFEN', sofern nicht anders angegeben
CREATE OR REPLACE TRIGGER TRBI_MWF_MELDUNG BEFORE INSERT OR UPDATE ON T_MWF_MELDUNG
FOR EACH ROW
  BEGIN
    IF (:new.sms_status IS NULL)
    THEN
      :new.sms_status := 'OFFEN';
    END IF;
    IF (:new.email_status IS NULL)
    THEN
      :new.email_status := 'OFFEN';
    END IF;
  END;
/