-- ANF-325 (HUR-19676) cps.dsl.data zu CPS - FTTX (Telefon) hinzufuegen
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
        (SELECT
           ID
         FROM T_SERVICE_COMMANDS
         WHERE NAME = 'cps.dsl.data'),
        (SELECT
           ID
         FROM T_SERVICE_CHAIN
         WHERE NAME = 'CPS - FTTX (Telefon)'), 'de.augustakom.hurrican.model.cc.command.ServiceChain', 6, 0);
