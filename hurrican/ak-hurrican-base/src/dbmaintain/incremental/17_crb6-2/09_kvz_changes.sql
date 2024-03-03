-- Aenderungen fuer KVZ aufnehmen

alter table T_UEVT drop column KVZ_NUMMER;

alter table T_EQUIPMENT add KVZ_DA VARCHAR2(4);



