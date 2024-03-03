DROP INDEX IX_FK_P2SIPD_SW_2_REF;
ALTER TABLE T_PROD_2_SIP_DOMAIN drop constraint FK_P2SIPD_SW_2_REF;

UPDATE T_PROD_2_SIP_DOMAIN p2s SET p2s.SWITCH_REF_ID = (SELECT s.ID FROM T_REFERENCE r INNER JOIN T_HW_SWITCH s ON s.NAME = r.STR_VALUE WHERE r.id = p2s.SWITCH_REF_ID);

CREATE INDEX IX_FK_P2SIPD_SW_2_HWSWITCH ON T_PROD_2_SIP_DOMAIN (SWITCH_REF_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_PROD_2_SIP_DOMAIN ADD CONSTRAINT FK_P2SIPD_SW_2_HWSWITCH
  FOREIGN KEY (SWITCH_REF_ID) REFERENCES T_HW_SWITCH (ID);