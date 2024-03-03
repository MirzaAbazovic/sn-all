----------------------------------------------------
------------ T_AUFTRAG_SIP_INTER_TRUNK -------------
----------------------------------------------------
DROP INDEX IX_FK_AUFTRAGSIPINT_2_REF;
ALTER TABLE T_AUFTRAG_SIP_INTER_TRUNK drop constraint FK_AUFTRAGSIPINT_2_REF;

UPDATE T_AUFTRAG_SIP_INTER_TRUNK asit SET asit.SWITCH_REF_ID =
    (SELECT s.ID FROM T_REFERENCE r INNER JOIN T_HW_SWITCH s ON s.NAME = r.STR_VALUE WHERE r.id = asit.SWITCH_REF_ID);

CREATE INDEX IX_FK_AUFTRAGSIPINT_2_HWSWITCH ON T_AUFTRAG_SIP_INTER_TRUNK (SWITCH_REF_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_AUFTRAG_SIP_INTER_TRUNK ADD CONSTRAINT FK_AUFTRAGSIPINT_2_HWSWITCH
  FOREIGN KEY (SWITCH_REF_ID) REFERENCES T_HW_SWITCH (ID);

----------------------------------------------------
------------------- T_PRODUKT ----------------------
----------------------------------------------------
ALTER TABLE T_PRODUKT drop constraint FK_PRODUKT_SWITCH;

UPDATE T_PRODUKT pr SET pr.SWITCH =
    (SELECT s.ID FROM T_REFERENCE r INNER JOIN T_HW_SWITCH s ON s.NAME = r.STR_VALUE WHERE r.id = pr.SWITCH);

ALTER TABLE T_PRODUKT ADD CONSTRAINT FK_PRODUKT_SWITCH
  FOREIGN KEY (SWITCH) REFERENCES T_HW_SWITCH (ID);

----------------------------------------------------
------------------- T_EQUIPMENT --------------------
----------------------------------------------------
CREATE INDEX IX_EQ_SWITCH ON T_EQUIPMENT (SWITCH) TABLESPACE "I_HURRICAN";
-- set switch to MUC06 for the one entry which has MUC07 as switch, which was wrong
UPDATE T_EQUIPMENT SET SWITCH = 'MUC06' where SWITCH = 'MUC07';
-- create temporary switch column
ALTER TABLE T_EQUIPMENT ADD TEMP_SWITCH NUMBER(19, 0);
-- copy switch id from t_hw_switch to the temp switch column
UPDATE T_EQUIPMENT eq SET eq.TEMP_SWITCH = (select s.ID FROM T_HW_SWITCH s WHERE s.NAME = eq.SWITCH);
-- drop column 'SWITCH' which contains the name of the switch
ALTER TABLE T_EQUIPMENT DROP COLUMN SWITCH;
-- rename temporary column to 'SWITCH'
ALTER TABLE T_EQUIPMENT RENAME COLUMN TEMP_SWITCH TO SWITCH;

CREATE INDEX IX_FK_EQ_HWSWITCH ON T_EQUIPMENT (SWITCH) TABLESPACE "I_HURRICAN";
ALTER TABLE T_EQUIPMENT ADD CONSTRAINT FK_EQ_HWSWITCH
  FOREIGN KEY (SWITCH) REFERENCES T_HW_SWITCH (ID);

----------------------------------------------------
------------------- T_HW_BG_CHANGE_DLU -------------
----------------------------------------------------
ALTER TABLE T_HW_BG_CHANGE_DLU ADD TEMP_SWITCH NUMBER(19, 0);
UPDATE T_HW_BG_CHANGE_DLU bgc SET bgc.TEMP_SWITCH = (select s.ID FROM T_HW_SWITCH s WHERE s.NAME = bgc.DLU_SWITCH_NEW);
ALTER TABLE T_HW_BG_CHANGE_DLU DROP COLUMN DLU_SWITCH_NEW;
ALTER TABLE T_HW_BG_CHANGE_DLU RENAME COLUMN TEMP_SWITCH TO DLU_SWITCH_NEW;

CREATE INDEX IX_FK_HW_BG_CHNG_DLU_HWSWITCH ON T_HW_BG_CHANGE_DLU (DLU_SWITCH_NEW) TABLESPACE "I_HURRICAN";
ALTER TABLE T_HW_BG_CHANGE_DLU ADD CONSTRAINT FK_HW_BG_CHNG_DLU_HWSWITCH
  FOREIGN KEY (DLU_SWITCH_NEW) REFERENCES T_HW_SWITCH (ID);

----------------------------------------------------
------------------- T_HW_RACK_LTG ------------------
----------------------------------------------------
-- create temporary switch column
ALTER TABLE T_HW_RACK_LTG ADD TEMP_SWITCH NUMBER(19, 0);
-- copy switch id from t_hw_switch to the temp switch column
UPDATE T_HW_RACK_LTG ltg SET ltg.TEMP_SWITCH = (select s.ID FROM T_HW_SWITCH s WHERE s.NAME = ltg.SWITCH);
-- drop column 'SWITCH' which contains the name of the switch
ALTER TABLE T_HW_RACK_LTG DROP COLUMN SWITCH;
-- rename temporary column to 'SWITCH'
ALTER TABLE T_HW_RACK_LTG RENAME COLUMN TEMP_SWITCH TO SWITCH;

CREATE INDEX IX_FK_HW_RACK_LTG_HWSWITCH ON T_HW_RACK_LTG (SWITCH) TABLESPACE "I_HURRICAN";
ALTER TABLE T_HW_RACK_LTG ADD CONSTRAINT FK_HW_RACK_LTG_HWSWITCH
  FOREIGN KEY (SWITCH) REFERENCES T_HW_SWITCH (ID);

----------------------------------------------------
------------------- T_HW_RACK_DLU ------------------
----------------------------------------------------
-- create temporary switch column
ALTER TABLE T_HW_RACK_DLU ADD TEMP_SWITCH NUMBER(19, 0);
-- copy switch id from t_hw_switch to the temp switch column
UPDATE T_HW_RACK_DLU dlu SET dlu.TEMP_SWITCH = (select s.ID FROM T_HW_SWITCH s WHERE s.NAME = dlu.SWITCH);
-- drop column 'SWITCH' which contains the name of the switch
ALTER TABLE T_HW_RACK_DLU DROP COLUMN SWITCH;
-- rename temporary column to 'SWITCH'
ALTER TABLE T_HW_RACK_DLU RENAME COLUMN TEMP_SWITCH TO SWITCH;

CREATE INDEX IX_FK_HW_RACK_DLU_HWSWITCH ON T_HW_RACK_DLU (SWITCH) TABLESPACE "I_HURRICAN";
ALTER TABLE T_HW_RACK_DLU ADD CONSTRAINT FK_HW_RACK_DLU_HWSWITCH
  FOREIGN KEY (SWITCH) REFERENCES T_HW_SWITCH (ID);
