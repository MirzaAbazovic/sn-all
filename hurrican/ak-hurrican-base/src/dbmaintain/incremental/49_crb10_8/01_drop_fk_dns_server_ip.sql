-- FK DNS_SERVER_IP_ID loeschen
DROP INDEX IX_FK_EGC_DSIPID_2_IPADDR;
ALTER TABLE T_EG_CONFIG DROP COLUMN DNS_SERVER_IP_ID;