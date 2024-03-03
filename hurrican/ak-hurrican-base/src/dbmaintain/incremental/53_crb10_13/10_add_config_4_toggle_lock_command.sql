-- ToggleLockEinwahlaccountCommand als Service Command konfigurieren (Zugaenge)
INSERT INTO T_SERVICE_COMMANDS
   (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
 VALUES
   (1010, 'disable.voip.einwahlaccount',
   'de.augustakom.hurrican.service.cc.impl.command.leistung.DisableEinwahlaccountCommand',
   'LS_ZUGANG', 'Sperrt das Internet Account anhand techn. Leistungen',
   0);

-- ToggleLockEinwahlaccountCommand als Service Command konfigurieren (Kuendigungen)
INSERT INTO T_SERVICE_COMMANDS
   (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
 VALUES
   (1011, 'enable.voip.einwahlaccount',
   'de.augustakom.hurrican.service.cc.impl.command.leistung.EnableEinwahlaccountCommand',
   'LS_KUENDIGUNG', 'Entsperrt das Internet Account anhand techn. Leistungen',
   0);

-- Map Service-Command (LS_ZUGANG) to TechLeistung (VOIP PMX)
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 1010, 302, 'de.augustakom.hurrican.model.cc.TechLeistung', NULL, 0);

-- Map Service-Command (LS_ZUGANG) to TechLeistung (5000 kbit/s, UPSTREAM)
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 1010, 27, 'de.augustakom.hurrican.model.cc.TechLeistung', NULL, 0);

-- Map Service-Command (LS_ZUGANG) to TechLeistung (2500 kbit/s, UPSTREAM)
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 1010, 114, 'de.augustakom.hurrican.model.cc.TechLeistung', NULL, 0);
    
-- Map Service-Command (LS_KUENDIGUNG) to TechLeistung (VOIP PMX)
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 1011, 302, 'de.augustakom.hurrican.model.cc.TechLeistung', NULL, 0);

-- Map Service-Command (LS_KUENDIGUNG) to TechLeistung (5000 kbit/s, UPSTREAM)
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 1011, 27, 'de.augustakom.hurrican.model.cc.TechLeistung', NULL, 0);
    
-- Map Service-Command (LS_KUENDIGUNG) to TechLeistung (5000 kbit/s, UPSTREAM)
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 1011, 114, 'de.augustakom.hurrican.model.cc.TechLeistung', NULL, 0);
