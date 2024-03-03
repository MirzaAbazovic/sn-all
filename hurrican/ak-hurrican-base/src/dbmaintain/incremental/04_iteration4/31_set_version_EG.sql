--
-- setzt die version für Hibernate auf 1
--

update T_PORT_FORWARD
 set version = 1;

update T_EG_CONFIG
set version = 1;

update T_EG_2_AUFTRAG
set version = 1;

update T_EG_IP
set version = 1;

update T_EG_ROUTING
set version = 1;
