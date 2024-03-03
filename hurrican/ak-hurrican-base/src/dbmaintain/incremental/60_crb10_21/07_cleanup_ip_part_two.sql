alter table t_eg_config drop column TRIGGERPUNKT;
alter table t_eg_config drop column DHCP_POOL_TO;
alter table t_eg_config drop column DHCP_POOL_FROM;

alter table t_port_forward drop column DEST_IP_ADDRESS;
alter table t_port_forward drop column SOURCE_IP_ADDRESS;
