INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
  VALUES (4006, 'check.rufnummer', 'de.augustakom.hurrican.service.cc.impl.command.tal.CheckRufnummerTALCommand',
  'TAL_BESTELLUNG_CHECK', 'Command prueft die Zulassung aller Rufnummern fuer die TAL Bestellung (z.B. Laenge der Rufnummer)', 0);

-- TAL-Neubestellung
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 4006, 40, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 6, 0);

-- TAL-Kuendigung
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 4006, 41, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 6, 0);

-- TAL-Storno
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 4006, 42, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 6, 0);

-- TAL-Nutzungsaenderung
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 4006, 43, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 6, 0);