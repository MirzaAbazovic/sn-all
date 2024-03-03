INSERT INTO T_SERVICE_COMMANDS (ID, NAME,
                                CLASS,
                                TYPE, DESCRIPTION, VERSION)
  VALUES (2026, 'verl.check.voip.dnplan',
          'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckVoipDnPlanCommand',
          'VERLAUF_CHECK', 'Command validiert alle zum Realisierungsdatum gültigen sowie alle zukünftigen VoIP-Rufnummernpläne', 0)
;

INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID,
                                       REF_CLASS, ORDER_NO, VERSION)
  VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextval, 2026, (SELECT
                                                      ID
                                                      FROM T_SERVICE_CHAIN
                                                      WHERE NAME = 'VoIP_GK_with_ShortTerm_Check'),
          'de.augustakom.hurrican.model.cc.command.ServiceChain', 20, 0)
;

INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID,
                                       REF_CLASS, ORDER_NO, VERSION)
  VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextval, 2026, (SELECT
                                                      ID
                                                      FROM T_SERVICE_CHAIN
                                                      WHERE NAME = 'SIP Trunk Check'),
          'de.augustakom.hurrican.model.cc.command.ServiceChain', 20, 0)
;
