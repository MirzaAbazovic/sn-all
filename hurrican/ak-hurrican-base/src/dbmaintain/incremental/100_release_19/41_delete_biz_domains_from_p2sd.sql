-- Alle biz Sip-Domaenen aus Prod2SipDomain entfernen, da diese fuer die Audiocodes reserviert sind

DELETE FROM T_PROD_2_SIP_DOMAIN
WHERE PROD_ID IS NULL AND SIP_DOMAIN_REF_ID IN (SELECT ID
                                                FROM T_REFERENCE
                                                WHERE TYPE='SIP_DOMAIN_TYPE' AND STR_VALUE LIKE 'biz%');
