DROP TRIGGER TRBIU_T_ANSPRECHPARTNER;

CREATE OR REPLACE TRIGGER TRAIU_T_ANSPRECHPARTNER
AFTER INSERT OR UPDATE ON T_ANSPRECHPARTNER
DECLARE
    CURSOR existingCursor IS
        SELECT ap.AUFTRAG_ID, ap.TYPE_REF_ID FROM T_ANSPRECHPARTNER ap
            WHERE ap.PREFERRED <> '0' AND ap.PREFERRED <> 'N'
            GROUP BY ap.AUFTRAG_ID, ap.TYPE_REF_ID
            HAVING COUNT(*) > 1;
    existing existingCursor%ROWTYPE;
BEGIN
    OPEN existingCursor;
    FETCH existingCursor INTO existing;
    IF existingCursor%FOUND THEN
        CLOSE existingCursor;
        RAISE_APPLICATION_ERROR(-20000, 'two preferred Ansprechparther not allowed for same Auftrag: '
            || existing.AUFTRAG_ID
            || ') and Type (id: '
            || existing.TYPE_REF_ID
            || ')');
    ELSE
        CLOSE existingCursor;
    END IF;
END;
/
