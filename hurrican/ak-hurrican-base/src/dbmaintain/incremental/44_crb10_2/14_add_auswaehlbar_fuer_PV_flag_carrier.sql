-- Da nur ein Teil der Carriers auswählbar für PV sind müssen diese filtriert werden --
-- -> Flag neu eingeführt in T_CARRIER

alter table T_CARRIER add AUSWAEHLBAR_PV char(1) DEFAULT 0;

update T_CARRIER set AUSWAEHLBAR_PV = 1
    where TEXT in ('DTAG', 'O2', 'Telefonica', 'QSC', 'Hansenet', 'eplus');
