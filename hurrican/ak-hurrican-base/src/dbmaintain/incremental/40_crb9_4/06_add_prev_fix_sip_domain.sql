-- ****************************************************************************
-- SIP Domain Referenz anlegen
-- ****************************************************************************
INSERT INTO T_REFERENCE
   (ID, TYPE, STR_VALUE, INT_VALUE, FLOAT_VALUE,
    UNIT_ID, GUI_VISIBLE, ORDER_NO, DESCRIPTION, VERSION)
 VALUES
   (22343, 'SIP_DOMAIN_TYPE', 'ngn.m-online.net', NULL, NULL,
    NULL, '1', 40, 'vormals fix provisionierte SIP Domäne', 0);

-- ****************************************************************************
-- alle alten VoIP DNs umschreiben
-- ****************************************************************************
UPDATE T_AUFTRAG_VOIP_DN set SIP_DOMAIN_REF_ID=22343 WHERE SIP_DOMAIN_REF_ID IS NULL;