-- ist bereits auf manchen umgebungen (sit01) angelegt
DECLARE
  cnt NUMBER(19);
BEGIN
  SELECT count(id)
  INTO cnt
  FROM T_REFERENCE
  WHERE id = 16015;
  IF (cnt = 0)
  THEN
    INSERT INTO T_REFERENCE
    (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE,
     ORDER_NO, DESCRIPTION, VERSION)
    VALUES
      (16015, 'ANSPRECHPARTNER', 'Kontakt für Sicherheitsabfragen', 204, '1',
       180, 'Kontakt für Sicherheitsabfragen', 0);
  END IF;
END;
/
