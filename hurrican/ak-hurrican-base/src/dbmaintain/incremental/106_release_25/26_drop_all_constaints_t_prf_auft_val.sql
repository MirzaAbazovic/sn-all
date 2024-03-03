declare
  CURSOR fke_cur IS
  SELECT table_name, constraint_name
  FROM user_constraints
  WHERE TABLE_NAME = 'T_PROFILE_AUFTRAG_VALUE';
  exStr VARCHAR2(4000);
BEGIN
  FOR fke_rec IN fke_cur
  LOOP
    exStr := 'ALTER TABLE ' || fke_rec.table_name || ' DROP CONSTRAINT ' || fke_rec.constraint_name;
    EXECUTE IMMEDIATE exStr;
  END LOOP;
END;
/

