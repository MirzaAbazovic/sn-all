alter table T_EG_CONFIG add (WAN_IP_FEST CHAR(1 BYTE));

update T_EG_CONFIG set WAN_IP_FEST=0;