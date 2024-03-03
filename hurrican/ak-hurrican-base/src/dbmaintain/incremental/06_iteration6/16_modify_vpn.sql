--
-- Tabelle T_VPN um einen eindeutigen VPN-Namen erweitern
--

alter table T_VPN add VPN_NAME VARCHAR2(6);
ALTER TABLE T_VPN ADD CONSTRAINT UK_T_VPN_NAME UNIQUE (VPN_NAME);
