ALTER TABLE T_AUFTRAG_TECHNIK ADD SWITCH_REF_ID NUMBER(19, 0) NULL;

ALTER TABLE T_AUFTRAG_TECHNIK ADD CONSTRAINT FK_AUFTECH_2_HWSWITCH
FOREIGN KEY (SWITCH_REF_ID) REFERENCES T_HW_SWITCH (ID);