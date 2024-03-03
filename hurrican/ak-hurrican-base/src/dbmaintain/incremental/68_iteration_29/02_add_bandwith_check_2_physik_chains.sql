-- Command registrieren
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
    VALUES (30, 'check.downstream.bandwidth',
      'de.augustakom.hurrican.service.cc.impl.command.physik.CheckDownstreamBandwidthCommand',
      'PHYSIK', 'Ueberprueft, ob die Downstream Bandbreite des neuen Auftrags mit der Physik des alten Auftrages realisiert werden kann.', 0);

-- PHYSIK Service Chain 'Anschlussübernahme - Standard'
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Luecke in Reihenfolge der Commands schaffen zwischen 2 und 3 (3 und groesser um +1 hochzaehlen, bei 11 ist bereits eine Luecke)  
UPDATE T_SERVICE_COMMAND_MAPPING SET ORDER_NO = ORDER_NO + 1 
    WHERE REF_CLASS = 'de.augustakom.hurrican.model.cc.command.ServiceChain' 
        AND REF_ID = 1 
        AND ORDER_NO in (10, 9, 8, 7, 6, 5, 4, 3);

-- Mapping fuer registriertes Command in Luecke anlegen (order_no = 3)
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 30,
      (SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschlussübernahme - Standard' AND c.TYPE = 'PHYSIK'), 
      'de.augustakom.hurrican.model.cc.command.ServiceChain', 3, 0);

-- PHYSIK Service Chain 'Anschlussübernahme - SDSL'
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Luecke in Reihenfolge der Commands schaffen zwischen 1 und 2 (2 und groesser um +1 hochzaehlen, bei 9 ist bereits eine Luecke)  
UPDATE T_SERVICE_COMMAND_MAPPING SET ORDER_NO = ORDER_NO + 1 
    WHERE REF_CLASS = 'de.augustakom.hurrican.model.cc.command.ServiceChain' 
        AND REF_ID = 2 
        AND ORDER_NO in (8, 7, 6, 5, 4, 3, 2);

-- Mapping fuer registriertes Command in Luecke anlegen (order_no = 2)
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 30,
      (SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschlussübernahme - SDSL' AND c.TYPE = 'PHYSIK'), 
      'de.augustakom.hurrican.model.cc.command.ServiceChain', 2, 0);

-- PHYSIK Service Chain 'DSL-Kreuzung - Produktwechsel'
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Luecke in Reihenfolge der Commands schaffen zwischen 1 und 2 (2 und groesser um +1 hochzaehlen)  
UPDATE T_SERVICE_COMMAND_MAPPING SET ORDER_NO = ORDER_NO + 1 
    WHERE REF_CLASS = 'de.augustakom.hurrican.model.cc.command.ServiceChain' 
        AND REF_ID = 5 
        AND ORDER_NO in (6, 5, 4, 3, 2);

-- Mapping fuer registriertes Command in Luecke anlegen (order_no = 2)
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 30,
      (SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='DSL-Kreuzung - Produktwechsel' AND c.TYPE = 'PHYSIK'), 
      'de.augustakom.hurrican.model.cc.command.ServiceChain', 2, 0);
