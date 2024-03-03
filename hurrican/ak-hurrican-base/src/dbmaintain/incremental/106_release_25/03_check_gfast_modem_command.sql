insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
  select 2032, 'verl.check.gfast.modem', 'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckGFastModemCommand',
        'VERLAUF_CHECK', 'Command prueft, ob ein G.Fast Modem zugeordnet ist.', 0
from dual
where not exists ( select 1 from T_SERVICE_COMMANDS tt where tt.id = 2032)
;
