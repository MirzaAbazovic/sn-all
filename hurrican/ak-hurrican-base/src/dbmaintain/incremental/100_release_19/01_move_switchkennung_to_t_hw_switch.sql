-- create temporary name column
ALTER TABLE T_HW_SWITCH ADD TEMP_NAME VARCHAR2(30);
-- copy switch name from t_reference to the temp name column
UPDATE T_HW_SWITCH s SET s.TEMP_NAME = (select r.STR_VALUE FROM T_REFERENCE r WHERE r.ID = s.NAME);
-- drop column 'NAME' which contains the reference to t_reference
ALTER TABLE T_HW_SWITCH DROP COLUMN NAME;
-- rename temporary column to 'NAME'
ALTER TABLE T_HW_SWITCH RENAME COLUMN TEMP_NAME TO NAME;
