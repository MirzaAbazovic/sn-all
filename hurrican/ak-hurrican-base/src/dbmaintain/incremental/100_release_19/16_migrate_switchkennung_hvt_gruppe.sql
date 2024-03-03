----------------------------------------------------
------------------- T_HVT_GRUPPE -------------------
----------------------------------------------------
update T_HVT_GRUPPE set SWITCH = 'MUC06' where SWITCH in ('ME01.Arnul214-ess1', 'MUC-06');
update T_HVT_GRUPPE set SWITCH = 'MUC04' where SWITCH in ('MNET4');

ALTER TABLE T_HVT_GRUPPE ADD TEMP_SWITCH NUMBER(19, 0);
-- copy switch id from t_hw_switch to the temp switch column
UPDATE T_HVT_GRUPPE hvtgr SET hvtgr.TEMP_SWITCH = (select s.ID FROM T_HW_SWITCH s WHERE s.NAME = hvtgr.SWITCH);
-- drop column 'SWITCH' which contains the name of the switch
ALTER TABLE T_HVT_GRUPPE DROP COLUMN SWITCH;
-- rename temporary column to 'SWITCH'
ALTER TABLE T_HVT_GRUPPE RENAME COLUMN TEMP_SWITCH TO SWITCH;

CREATE INDEX IX_FK_HVT_GRUPPE_HWSWITCH ON T_HVT_GRUPPE (SWITCH) TABLESPACE "I_HURRICAN";
ALTER TABLE T_HVT_GRUPPE ADD CONSTRAINT FK_HVT_GRUPPE_HWSWITCH
  FOREIGN KEY (SWITCH) REFERENCES T_HW_SWITCH (ID);