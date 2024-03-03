-- Rename Service-Command with TDN in name

UPDATE   T_SERVICE_COMMANDS sc
   SET   SC.DESCRIPTION =
            'Uebertraegt die Verbindungsbezeichnung des Ursprungs- auf den Ziel-Auftrag.'
 WHERE   sc.class =
            'de.augustakom.hurrican.service.cc.impl.command.physik.MoveVbzCommand'
/
