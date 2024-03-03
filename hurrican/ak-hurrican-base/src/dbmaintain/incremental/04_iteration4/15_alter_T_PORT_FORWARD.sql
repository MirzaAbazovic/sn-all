--
-- Modifies the T_PORT_FORWARD table for the new stucture of port forwardings
-- with source ip,port and destination ip,port
--


-- First drop some constraints
alter table T_PORT_FORWARD drop constraint FK_PORTFW_2_REF;
alter table T_PORT_FORWARD modify IP_ENDGERAET__ID null;
alter table T_PORT_FORWARD modify PROTOCOL null;
alter table T_PORT_FORWARD modify IP_ADDRESS null;

-- change and add columns
alter table T_PORT_FORWARD add TRANSPORT_PROTOCOL VARCHAR2(50 BYTE);
comment on column T_PORT_FORWARD.TRANSPORT_PROTOCOL
  is 'Transportprotokoll des Port-Forwadings (z.B. tcp, udp).';

alter table T_PORT_FORWARD rename column IP_ADDRESS to SOURCE_IP_ADDRESS;

alter table T_PORT_FORWARD add DEST_IP_ADDRESS VARCHAR2(15 BYTE);
comment on column T_PORT_FORWARD.DEST_IP_ADDRESS
  is 'Ziel IP-Adresse der Port-Weiterleitung.';

alter table T_PORT_FORWARD add SOURCE_PORT NUMBER(10);
comment on column T_PORT_FORWARD.SOURCE_PORT
  is 'Portnummer des Quellports der weitergeleitet werden soll.';

alter table T_PORT_FORWARD add DEST_PORT NUMBER(10);
comment on column T_PORT_FORWARD.DEST_PORT
  is 'Ziel Portnummer der Port-Weiterleitung.';

alter table T_PORT_FORWARD add EG_CONFIG_ID NUMBER(10);
comment on column T_PORT_FORWARD.EG_CONFIG_ID
  is 'Fremdschluessel zur zugehoerigen Endgeraete-Config.';

-- update T_REFERENCE entries with port numbers of the different protocols
update T_REFERENCE set INT_VALUE=80 where ID=6000;
update T_REFERENCE set INT_VALUE=143 where ID=6001;
update T_REFERENCE set INT_VALUE=110 where ID=6002;
update T_REFERENCE set INT_VALUE=25 where ID=6003;
update T_REFERENCE set INT_VALUE=443 where ID=6004;
update T_REFERENCE set INT_VALUE=993 where ID=6005;
update T_REFERENCE set INT_VALUE=995 where ID=6006;
update T_REFERENCE set INT_VALUE=515 where ID=6007;
update T_REFERENCE set INT_VALUE=22 where ID=6008;
update T_REFERENCE set INT_VALUE=1194 where ID=6009;
update T_REFERENCE set INT_VALUE=1195 where ID=6010;
update T_REFERENCE set INT_VALUE=6387 where ID=6011;
update T_REFERENCE set INT_VALUE=500 where ID=6012;

-- migrate data
update T_PORT_FORWARD F set SOURCE_PORT =
    (select INT_VALUE from T_REFERENCE R where R.ID=f.PROTOCOL);

update T_PORT_FORWARD F set TRANSPORT_PROTOCOL = 'udp'
     where F.PROTOCOL=6012;

update T_PORT_FORWARD F set EG_CONFIG_ID =
    (select max(ID) from T_EG_CONFIG C where C.IP_ENDGERAET__ID = F.IP_ENDGERAET__ID);


-- remove obsolete columns
alter table T_PORT_FORWARD drop column PROTOCOL;
alter table T_PORT_FORWARD drop column IP_ENDGERAET__ID;
alter table T_EG_CONFIG drop column IP_ENDGERAET__ID;

-- add new constraints
alter table T_PORT_FORWARD modify SOURCE_PORT not null;
alter table T_PORT_FORWARD modify SOURCE_IP_ADDRESS not null;
alter table T_PORT_FORWARD add constraint FK_PORTFW_2_EG_CONFIG
    foreign key (EG_CONFIG_ID) references T_EG_CONFIG (ID);

