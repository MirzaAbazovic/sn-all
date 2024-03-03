-- create new service-chain
INSERT INTO T_SERVICE_CHAIN (ID, NAME, TYPE, DESCRIPTION, VERSION)
    VALUES (125, 'VoIP_GK_with_ShortTerm_Check', 'VERLAUF_CHECK', 'Verlauf-Chain fuer Premium Glasfaser-DSL Doppel-Flat (540) und Glasfaser SDSL (541)', 0);


-- mapping service-commands to service-chain
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2013, 125, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10, 0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2010, 125, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20, 0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2021, 125, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 30, 0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2018, 125, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 40, 0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2008, 125, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 50, 0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2014, 125, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 60, 0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2017, 125, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 70, 0);