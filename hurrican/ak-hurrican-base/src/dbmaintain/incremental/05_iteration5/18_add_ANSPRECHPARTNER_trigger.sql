CREATE OR REPLACE TRIGGER TRBIU_T_ANSPRECHPARTNER
BEFORE INSERT OR UPDATE ON T_ANSPRECHPARTNER
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
DECLARE
    existing NUMBER(10);
BEGIN
    IF :NEW.PREFERRED <> '0' AND :NEW.PREFERRED <> 'N' THEN
        SELECT COUNT(*) INTO existing FROM T_ANSPRECHPARTNER ap
            WHERE ap.PREFERRED <> '0' AND ap.PREFERRED <> 'N'
			  AND ap.AUFTRAG_ID = :NEW.AUFTRAG_ID AND ap.TYPE_REF_ID = :NEW.TYPE_REF_ID;
        IF existing > 0 THEN
            RAISE_APPLICATION_ERROR(-20000, 'two preferred Ansprechparther not allowed for same Auftrag (id: '
				|| :NEW.AUFTRAG_ID
				|| ') and Type (id: '
				|| :NEW.TYPE_REF_ID
				|| ')');
        END IF;
    END IF; 
END;
/
