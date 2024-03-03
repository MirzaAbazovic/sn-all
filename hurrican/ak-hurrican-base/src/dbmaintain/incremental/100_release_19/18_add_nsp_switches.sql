-- add unique constraint on the name column
ALTER TABLE T_HW_SWITCH ADD CONSTRAINT UC_HW_SWITCH_NAME UNIQUE (NAME);

-- create the new nsp switches
INSERT INTO T_HW_SWITCH (ID, TYPE, NAME) VALUES (S_T_HW_SWITCH_0.nextVal, 'NSP', 'MUC07');
INSERT INTO T_HW_SWITCH (ID, TYPE, NAME) VALUES (S_T_HW_SWITCH_0.nextVal, 'NSP', 'MUC08');
