alter table T_HVT_UMZUG_DETAIL add CPS_TX_ID NUMBER(19,0);

ALTER TABLE T_HVT_UMZUG_DETAIL ADD CONSTRAINT FK_HVTUD_2_CPS
  FOREIGN KEY (CPS_TX_ID) REFERENCES T_CPS_TX (ID);