-- Verlauf Chain
INSERT INTO T_SERVICE_CHAIN
   (ID, NAME, TYPE, DESCRIPTION, VERSION)
 VALUES
   (60, 'dsl+VoIP-Check', 'VERLAUF_CHECK', NULL, 0);
   
-- Command Mapping
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, 
    VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2001, 60, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10, 
    0);
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, 
    VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2009, 60, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20, 
    0);
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, 
    VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2004, 60, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 30, 
    0);
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, 
    VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2018, 60, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 40, 
    0);
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, 
    VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2005, 60, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 50, 
    0);
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, 
    VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2013, 60, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 60, 
    0);
    
-- Hier Sequence incrementieren, da ID scheinbar bereits manuell vergeben wurde...    
SELECT S_T_SERVICE_COMMAND_MAPPI_0.nextVal FROM DUAL;    

INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, 
    VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2000, 60, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 70, 
    0);
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, 
    VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2006, 60, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 80, 
    0);
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, 
    VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2007, 60, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 90, 
    0);
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, 
    VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2008, 60, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 100, 
    0);
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, 
    VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2017, 60, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 110, 
    0);
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, 
    VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2010, 60, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 120, 
    0);

-- verlauf-check
UPDATE T_PRODUKT P SET P.VERLAUF_CHAIN_ID = 60 WHERE P.PROD_ID = 480;

-- verlauf cancel
UPDATE T_PRODUKT P SET P.VERLAUF_CANCEL_CHAIN_ID = 13 WHERE P.PROD_ID = 480;   
