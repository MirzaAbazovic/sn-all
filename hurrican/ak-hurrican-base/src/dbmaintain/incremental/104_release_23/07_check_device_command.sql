insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
select 2031, 'verl.check.devices', 'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckDevicesCommand', 'VERLAUF_CHECK', 'Command prueft, ob ein FritzBox gebucht oder zugewiesen ist.', 0
from dual
where not exists ( select 1 from T_SERVICE_COMMANDS tt where tt.id = 2031)
;
