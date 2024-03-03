-- rename CheckZugangBandwidthCommand
update T_SERVICE_COMMANDS set NAME = 'check.zugang.bandwidth', 
    CLASS = 'de.augustakom.hurrican.service.cc.impl.command.leistung.CheckZugangBandwidthCommand', 
    DESCRIPTION = 'Check-Command um zu pruefen, ob die aus der tech. Leistung des Auftrags hervorgehende Downstream-Bandbreite' 
    ||' mit der dem Auftrag zugeordneten Physik/Hardware moeglich ist.' 
    where ID = 1000;
    
-- rename ZugangBandwidthCommand
update T_SERVICE_COMMANDS set NAME = 'zugang.bandwidth', 
    CLASS = 'de.augustakom.hurrican.service.cc.impl.command.leistung.ZugangBandwithCommand', 
    DESCRIPTION = 'Dieses Command leitet von CheckZugangBandwidthCommand (ID=1000) fuer die Leistung ab und wertet das CheckCommandResult-Objekt aus.' 
    where ID = 1001;
    
-- faelschlich konfigurierte Upstream Bandbreite 10.000 auf die Downstream Bandbreite 10.000 rekonfigurieren 
-- (für CheckZugangBandwidthCommand + ZugangBandwithCommand)
update T_SERVICE_COMMAND_MAPPING set REF_ID = 33 
    where REF_CLASS = 'de.augustakom.hurrican.model.cc.TechLeistung'
    AND REF_ID = 28
    AND COMMAND_ID IN (1000, 1001);
    
-- Downstream Bandbreite 100.000 (für CheckZugangBandwidthCommand + ZugangBandwithCommand)
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, VERSION)
    values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 1000, 25, 'de.augustakom.hurrican.model.cc.TechLeistung', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, VERSION)
    values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 1001, 25, 'de.augustakom.hurrican.model.cc.TechLeistung', 0);
