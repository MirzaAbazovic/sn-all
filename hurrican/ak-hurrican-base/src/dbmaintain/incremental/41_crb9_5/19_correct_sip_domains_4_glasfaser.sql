-- Bisherige 'ngn.m-online.net' als einzige default SIP Domain auf Glasfaser Produkte konfigurieren
UPDATE T_PROD_2_SIP_DOMAIN SET SWITCH_REF_ID=22204, SIP_DOMAIN_REF_ID=22343
    WHERE SWITCH_REF_ID=22343 AND SIP_DOMAIN_REF_ID=22204;
