BEGIN
  DECLARE
    cnt NUMBER(19);
  BEGIN
    SELECT count(*)
    INTO cnt
    FROM T_PROD_2_TECH_LEISTUNG
    WHERE prod_id = 540 AND tech_ls_id = 419;
    IF cnt = 0
    THEN
      INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT)
      VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 540, 419, '0');
    END IF;
  END;
END;
/
