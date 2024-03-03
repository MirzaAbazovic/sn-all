-- Tabelle T_EQUIPMENT um KVZ Nummer erweitern

alter table T_EQUIPMENT add KVZ_NUMMER VARCHAR2(5);

-- Ports von KVZ Amberg um KVZ Nummer erweitern
update T_EQUIPMENT set KVZ_NUMMER='A031' where HVT_ID_STANDORT=819 and CARRIER='DTAG';

