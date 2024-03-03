-- Update der vorinstallierten SIP-Domain Kenner

--  MUC07 alias mnet-voip
-- S&F
UPDATE T_REFERENCE SET STR_VALUE = 'phone.mnet-voip.de' WHERE TYPE = 'SIP_DOMAIN_TYPE' AND STR_VALUE = 'maxi.m-call.muc07.de';
-- POTS
UPDATE T_REFERENCE SET STR_VALUE = 'mgw.mnet-voip.de' WHERE TYPE = 'SIP_DOMAIN_TYPE' AND STR_VALUE = 'mgw.m-call.muc07.de';
-- MGA
UPDATE T_REFERENCE SET STR_VALUE = 'mga.mnet-voip.de' WHERE TYPE = 'SIP_DOMAIN_TYPE' AND STR_VALUE = 'mga.m-call.muc07.de';
-- Audiocodes (das ist ein Hersteller)
UPDATE T_REFERENCE SET STR_VALUE = 'biz.mnet-voip.de' WHERE TYPE = 'SIP_DOMAIN_TYPE' AND STR_VALUE = 'biz.m-call.muc07.de';
-- SIP-Trunk
UPDATE T_REFERENCE SET STR_VALUE = 'business.mnet-voip.de' WHERE TYPE = 'SIP_DOMAIN_TYPE' AND STR_VALUE = 'business.m-call.muc07.de';

-- MUC08 alias test.mnet-voip
-- S&F
UPDATE T_REFERENCE SET STR_VALUE = 'phone.test.mnet-voip.de' WHERE TYPE = 'SIP_DOMAIN_TYPE' AND STR_VALUE = 'maxi.m-call.muc08.de';
-- POTS
UPDATE T_REFERENCE SET STR_VALUE = 'mgw.test.mnet-voip.de' WHERE TYPE = 'SIP_DOMAIN_TYPE' AND STR_VALUE = 'mgw.m-call.muc08.de';
-- MGA
UPDATE T_REFERENCE SET STR_VALUE = 'mga.test.mnet-voip.de' WHERE TYPE = 'SIP_DOMAIN_TYPE' AND STR_VALUE = 'mga.m-call.muc08.de';
-- Audiocodes (das ist ein Hersteller)
UPDATE T_REFERENCE SET STR_VALUE = 'biz.test.mnet-voip.de' WHERE TYPE = 'SIP_DOMAIN_TYPE' AND STR_VALUE = 'biz.m-call.muc08.de';
-- SIP-Trunk
UPDATE T_REFERENCE SET STR_VALUE = 'business.test.mnet-voip.de' WHERE TYPE = 'SIP_DOMAIN_TYPE' AND STR_VALUE = 'business.m-call.muc08.de';
