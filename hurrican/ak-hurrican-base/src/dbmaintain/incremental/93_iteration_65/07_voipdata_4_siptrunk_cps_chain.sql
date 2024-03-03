insert into T_SERVICE_COMMAND_MAPPING (ID,
                                       COMMAND_ID,
                                       REF_ID,
                                       REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextval,
            (select id from T_SERVICE_COMMANDS where class = 'de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetVoIPDataCommand'),
            (select id from T_SERVICE_CHAIN where name = 'CPS-SIPTRUNK'),
            'de.augustakom.hurrican.model.cc.command.ServiceChain', 10, 0)
;
