--
-- Bugfix for the Hurrican->Hurrican migration of the PortForwardings to the new
-- Portforwarding structure
--

alter table T_PORT_FORWARD rename column SOURCE_IP_ADDRESS to TEMP_IP_ADDRESS;
alter table T_PORT_FORWARD rename column SOURCE_PORT to TEMP_PORT;

alter table T_PORT_FORWARD rename column DEST_IP_ADDRESS to SOURCE_IP_ADDRESS;
alter table T_PORT_FORWARD rename column DEST_PORT to SOURCE_PORT;

alter table T_PORT_FORWARD rename column TEMP_IP_ADDRESS to DEST_IP_ADDRESS;
alter table T_PORT_FORWARD rename column TEMP_PORT to DEST_PORT;


comment on column T_PORT_FORWARD.SOURCE_IP_ADDRESS
  is 'Quell-IP-Adresse der Port-Weiterleitung.';
comment on column T_PORT_FORWARD.SOURCE_PORT
  is 'Portnummer des Quellports der weitergeleitet werden soll.';


comment on column T_PORT_FORWARD.DEST_IP_ADDRESS
  is 'Ziel IP-Adresse der Port-Weiterleitung.';
comment on column T_PORT_FORWARD.DEST_PORT
  is 'Ziel Portnummer der Port-Weiterleitung.';

