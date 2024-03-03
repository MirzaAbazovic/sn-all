-- REALM fuer VPNs

alter table T_VPN add REALM VARCHAR2(30);
comment on column T_VPN.REALM is 'Account REALM fuer das VPN';
