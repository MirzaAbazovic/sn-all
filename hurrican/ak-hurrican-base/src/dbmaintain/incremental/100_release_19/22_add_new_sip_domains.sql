-- ANF-449: Alle neuen SIP-Domaenen anlegen

-- Beschreibungen vervollstaendigen
UPDATE T_REFERENCE SET DESCRIPTION = 'SIP Dom' || chr(228) || 'ne f' || chr(252) || 'r S&F' WHERE ID = 22344;
UPDATE T_REFERENCE SET DESCRIPTION = 'SIP Dom' || chr(228) || 'ne f' || chr(252) || 'r POTS' WHERE ID = 22345;
UPDATE T_REFERENCE SET DESCRIPTION = 'SIP Dom' || chr(228) || 'ne f' || chr(252) || 'r Audiocodes' WHERE ID = 22346;

-- mga.m-call.de
INSERT INTO T_REFERENCE
  (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
VALUES
  (22348, 'SIP_DOMAIN_TYPE', 'mga.m-call.de', '1', 90, 'SIP Dom' || chr(228) || 'ne f' || chr(252) || 'r MGA');

-- MUC07
INSERT INTO T_REFERENCE
  (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
VALUES
  (22349, 'SIP_DOMAIN_TYPE', 'maxi.m-call.muc07.de', '1', 100, 'SIP Dom' || chr(228) || 'ne f' || chr(252) || 'r S&F');

INSERT INTO T_REFERENCE
  (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
VALUES
  (22350, 'SIP_DOMAIN_TYPE', 'mgw.m-call.muc07.de', '1', 110, 'SIP Dom' || chr(228) || 'ne f' || chr(252) || 'r POTS');

INSERT INTO T_REFERENCE
  (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
VALUES
  (22351, 'SIP_DOMAIN_TYPE', 'mga.m-call.muc07.de', '1', 120, 'SIP Dom' || chr(228) || 'ne f' || chr(252) || 'r MGA');

INSERT INTO T_REFERENCE
  (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
VALUES
  (22352, 'SIP_DOMAIN_TYPE', 'biz.m-call.muc07.de', '1', 130, 'SIP Dom' || chr(228) || 'ne f' || chr(252) || 'r Audiocodes');

INSERT INTO T_REFERENCE
  (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
VALUES
  (22353, 'SIP_DOMAIN_TYPE', 'business.m-call.muc07.de', '1', 140, 'SIP Dom' || chr(228) || 'ne f' || chr(252) || 'r SIP-Trunk');

-- MUC08
INSERT INTO T_REFERENCE
  (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
VALUES
  (22354, 'SIP_DOMAIN_TYPE', 'maxi.m-call.muc08.de', '1', 150, 'SIP Dom' || chr(228) || 'ne f' || chr(252) || 'r S&F');

INSERT INTO T_REFERENCE
  (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
VALUES
  (22355, 'SIP_DOMAIN_TYPE', 'mgw.m-call.muc08.de', '1', 160, 'SIP Dom' || chr(228) || 'ne f' || chr(252) || 'r POTS');

INSERT INTO T_REFERENCE
  (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
VALUES
  (22356, 'SIP_DOMAIN_TYPE', 'mga.m-call.muc08.de', '1', 170, 'SIP Dom' || chr(228) || 'ne f' || chr(252) || 'r MGA');

INSERT INTO T_REFERENCE
  (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
VALUES
  (22357, 'SIP_DOMAIN_TYPE', 'biz.m-call.muc08.de', '1', 180, 'SIP Dom' || chr(228) || 'ne f' || chr(252) || 'r Audiocodes');

INSERT INTO T_REFERENCE
  (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
VALUES
  (22358, 'SIP_DOMAIN_TYPE', 'business.m-call.muc08.de', '1', 190, 'SIP Dom' || chr(228) || 'ne f' || chr(252) || 'r SIP-Trunk');
