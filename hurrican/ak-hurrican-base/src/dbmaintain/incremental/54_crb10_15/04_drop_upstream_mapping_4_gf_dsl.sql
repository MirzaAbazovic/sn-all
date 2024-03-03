-- Mapping Service-Command (LS_ZUGANG) to TechLeistung (2500 kbit/s, UPSTREAM) loeschen
DELETE FROM T_SERVICE_COMMAND_MAPPING WHERE COMMAND_ID=1010 AND REF_ID = 114 AND REF_CLASS = 'de.augustakom.hurrican.model.cc.TechLeistung';

-- Mapping Service-Command (LS_KUENDIGUNG) to TechLeistung (2500 kbit/s, UPSTREAM) loeschen
DELETE FROM T_SERVICE_COMMAND_MAPPING WHERE COMMAND_ID=1011 AND REF_ID = 114 AND REF_CLASS = 'de.augustakom.hurrican.model.cc.TechLeistung';
