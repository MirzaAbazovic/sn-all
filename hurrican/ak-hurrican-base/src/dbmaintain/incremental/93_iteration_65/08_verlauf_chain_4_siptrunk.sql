INSERT INTO T_SERVICE_CHAIN (ID, NAME, TYPE, DESCRIPTION, VERSION)
  VALUES (S_T_SERVICE_CHAIN_0.nextval, 'SIP Trunk Check', 'VERLAUF_CHECK', 'BA-chain fuer Produkt SIP Trunk', 0);

INSERT INTO T_SERVICE_COMMANDS (ID, NAME,
                                CLASS,
                                TYPE, DESCRIPTION, VERSION)
  VALUES (420, 'verlauf.check.onkzasb',
          'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckOnkzAsbAvailableCommand',
          'VERLAUF_CHECK', 'Command prüft ob onkz/asb für einen Auftrag ermittelt werden können', 0)
;

INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID,
                                       REF_CLASS, ORDER_NO, VERSION)
  VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextval, 420, (SELECT
                                                       id
                                                     FROM T_SERVICE_CHAIN
                                                     WHERE name = 'SIP Trunk Check'),
          'de.augustakom.hurrican.model.cc.command.ServiceChain', 10, 0)
;
