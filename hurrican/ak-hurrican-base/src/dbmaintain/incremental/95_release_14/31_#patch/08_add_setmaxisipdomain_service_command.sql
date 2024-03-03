INSERT INTO T_SERVICE_COMMANDS (ID,NAME,CLASS,TYPE,DESCRIPTION,VERSION)
    VALUES (1014,'set.sipdomain.maxi','de.augustakom.hurrican.service.cc.impl.command.leistung.SetMaxiSipDomainCommand',
    'LS_ZUGANG','Command-Klasse, um die SIP-Domaene auf "maxi" zu setzen',0);

INSERT INTO T_SERVICE_COMMAND_MAPPING (ID,COMMAND_ID,REF_ID,REF_CLASS,ORDER_NO,VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextval,1014,560,'de.augustakom.hurrican.model.cc.TechLeistung',null,0);

