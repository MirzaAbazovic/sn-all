alter table t_ip_route drop column IP_ADDRESS;
alter table t_ip_route drop column PREFIX_LENGTH;

alter table t_eg_config drop column DNS_SERVER_SUBNET;
alter table t_eg_routing drop column DESTINATION_NETMASK; 
alter table t_eg_routing drop column DESTINATION_ADRESS;
alter table t_eg_ip drop column SUBNET_MASK;
alter table t_eg_ip drop column IP_ADDRESS;
alter table t_eg_iad drop column IP_ADRESSE;
