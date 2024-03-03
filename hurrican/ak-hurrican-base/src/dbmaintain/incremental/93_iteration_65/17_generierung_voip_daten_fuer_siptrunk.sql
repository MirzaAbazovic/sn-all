-- VOIP-Daten für SIP-Trunk mit bestehendem Command generieren

-- Eintrag in T_SERVICE-COMMAND_MAPPING
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID,
                                       REF_CLASS, ORDER_NO, VERSION)
  VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextval, 1005, 470,
          'de.augustakom.hurrican.model.cc.TechLeistung', null, 0)
;


