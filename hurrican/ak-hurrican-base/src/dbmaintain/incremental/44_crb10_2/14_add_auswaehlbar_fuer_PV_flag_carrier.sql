-- Da nur ein Teil der Carriers ausw�hlbar f�r PV sind m�ssen diese filtriert werden --
-- -> Flag neu eingef�hrt in T_CARRIER

alter table T_CARRIER add AUSWAEHLBAR_PV char(1) DEFAULT 0;

update T_CARRIER set AUSWAEHLBAR_PV = 1
    where TEXT in ('DTAG', 'O2', 'Telefonica', 'QSC', 'Hansenet', 'eplus');
