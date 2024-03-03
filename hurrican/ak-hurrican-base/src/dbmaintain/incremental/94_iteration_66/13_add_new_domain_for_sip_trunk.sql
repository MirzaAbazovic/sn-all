--
-- SIP Domäne für SIP-Trunk anlegen
--
INSERT INTO T_REFERENCE
   (ID, TYPE, STR_VALUE, INT_VALUE, FLOAT_VALUE,
    UNIT_ID, GUI_VISIBLE, ORDER_NO, DESCRIPTION, VERSION)
 VALUES
   (22347, 'SIP_DOMAIN_TYPE', 'business.m-call.de', NULL, NULL,
    NULL, '1', 80, 'SIP Domäne für SIP-Trunk', 0);

--
-- SIP Domäne für SIP-Trunk ändern
--
UPDATE T_PROD_2_SIP_DOMAIN
  SET SIP_DOMAIN_REF_ID = 22347, USERW = 'IMPORT', DATEW = sysdate
  WHERE PROD_ID = 580;
