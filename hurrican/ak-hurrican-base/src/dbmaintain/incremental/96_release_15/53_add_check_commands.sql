-- neue Service Commands in DB anlegen
-- CheckDnPortierungskennungCommand
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
VALUES (2027, 'verl.check.dn.portierungskennung',
        'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckDnPortierungskennungCommand',
        'VERLAUF_CHECK', 'Command prueft alle Rufnummern, ob gueltige Portierungskennungen hinterlegt sind', 0);

-- CheckDnAgsnCommand
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
VALUES (2028, 'verl.check.dn.agsn', 'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckDnAgsnCommand',
        'VERLAUF_CHECK', 'Command prueft den AGSN der Geo-ID der Endstelle B', 0);
