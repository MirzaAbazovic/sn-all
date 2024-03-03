-- T_PORT_FORWARD
ALTER TABLE T_EG_ROUTING ADD (NEXT_HOP_ID NUMBER(19) NULL);
CREATE INDEX IX_FK_EGR_NH_2_IPADDR ON T_EG_ROUTING(NEXT_HOP_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_EG_ROUTING ADD CONSTRAINT FK_EGR_NH_2_IPADDR FOREIGN KEY (NEXT_HOP_ID) REFERENCES T_IP_ADDRESS (ID);

ALTER TABLE T_EG_ROUTING ADD (DESTINATION_ADRESS_ID NUMBER(19) NULL);
CREATE INDEX IX_FK_EGR_DADDR_2_IPADDR ON T_EG_ROUTING(DESTINATION_ADRESS_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_EG_ROUTING ADD CONSTRAINT FK_EGR_DADDR_2_IPADDR FOREIGN KEY (DESTINATION_ADRESS_ID) REFERENCES T_IP_ADDRESS (ID);
