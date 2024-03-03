--
-- Change passwort and account length to be big enough
-- for values from Kup
--


ALTER TABLE T_INT_ACCOUNT
MODIFY(ACCOUNT VARCHAR2(32 BYTE));


ALTER TABLE T_INT_ACCOUNT
MODIFY(PASSWORT VARCHAR2(32 BYTE));