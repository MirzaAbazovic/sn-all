-- SIP Domaenen Konfiguration auf NSP umstellen
-------------------------------------------------------
-- Switch zum Pflichtfeld machen
ALTER TABLE T_PROD_2_SIP_DOMAIN MODIFY HW_SWITCH NOT NULL;

-- PROD_ID nullable machen
ALTER TABLE T_PROD_2_SIP_DOMAIN MODIFY PROD_ID NULL;

-- Endgeraete FK
ALTER TABLE T_PROD_2_SIP_DOMAIN ADD EG_TYPE_ID NUMBER(19);
ALTER TABLE T_PROD_2_SIP_DOMAIN add constraint FK_P2SD_EGTYPE foreign key (EG_TYPE_ID) references T_EG_TYPE (ID);
