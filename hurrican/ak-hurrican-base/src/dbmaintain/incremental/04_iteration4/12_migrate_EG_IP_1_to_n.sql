--
-- Migriert die IPs der Endgeraete in die neue Datenstruktur
--

insert into t_eg_ip (id, eg2a_id, ip_address, subnet_mask, address_type) (
select s_t_eg_ip_0.nextval, eg2a_id, ip_lan, subnetmask, 'LAN' from t_eg_config
where ip_lan is not null);
