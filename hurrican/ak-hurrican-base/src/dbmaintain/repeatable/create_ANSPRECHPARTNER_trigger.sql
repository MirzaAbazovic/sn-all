create or replace TRIGGER TRBIU_T_ANSPRECHPARTNER
FOR INSERT OR UPDATE ON T_ANSPRECHPARTNER
COMPOUND TRIGGER

  CURSOR existingCursor(p_auftrag_id number) IS
      SELECT ap.AUFTRAG_ID, ap.TYPE_REF_ID FROM T_ANSPRECHPARTNER ap
          WHERE ap.PREFERRED <> '0' AND ap.PREFERRED <> 'N'
                and ap.AUFTRAG_ID = p_auftrag_id
          GROUP BY ap.AUFTRAG_ID, ap.TYPE_REF_ID
          HAVING COUNT(*) > 1;
  existing existingCursor%ROWTYPE;

  TYPE typ_ids IS TABLE OF number;
  vt_auftragIds typ_ids := typ_ids();
  v_cnt number := 0;

  BEFORE STATEMENT IS
  BEGIN
    NULL;
  END BEFORE STATEMENT;

  BEFORE EACH ROW IS
  BEGIN
    if  :new.AUFTRAG_ID is not null
    then
      v_cnt := v_cnt + 1;
      vt_auftragIds.extend;
      vt_auftragIds(v_cnt) := :new.AUFTRAG_ID;
    end if;
  END BEFORE EACH ROW;

  AFTER EACH ROW IS
  BEGIN
    null;
  END AFTER EACH ROW;

  AFTER STATEMENT IS
  BEGIN
    if vt_auftragIds.count > 0 then
        -- filter out duplicates
        vt_auftragIds := vt_auftragIds MULTISET UNION DISTINCT vt_auftragIds;

        FOR i IN vt_auftragIds.FIRST .. vt_auftragIds.LAST
        LOOP
          OPEN existingCursor(vt_auftragIds(i));
          FETCH existingCursor INTO existing;
          IF existingCursor%FOUND THEN
              CLOSE existingCursor;
              RAISE_APPLICATION_ERROR(-20000, 'two preferred Ansprechpartner not allowed for same Auftrag: '
                  || existing.AUFTRAG_ID
                  || ') and Type (id: '
                  || existing.TYPE_REF_ID
                  || ')');
          ELSE
              CLOSE existingCursor;
          END IF;
        END LOOP;

    end if;
  END AFTER STATEMENT;
END;
/
