-- Rename Service-Command with TDN in name

UPDATE   T_SERVICE_COMMANDS sc
   SET   SC.class =
            'de.augustakom.hurrican.service.cc.impl.command.physik.MoveVbzCommand'
 WHERE   sc.class =
            'de.augustakom.hurrican.service.cc.impl.command.physik.MoveTDNCommand'
/
