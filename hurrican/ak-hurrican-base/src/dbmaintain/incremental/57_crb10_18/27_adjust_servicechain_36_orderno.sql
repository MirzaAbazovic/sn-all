UPDATE T_SERVICE_COMMAND_MAPPING SET ORDER_NO = 10
    WHERE COMMAND_ID = (select c.ID from T_SERVICE_COMMANDS c where c.NAME='verl.check.auftrag.niederlassung')
    AND REF_CLASS = 'de.augustakom.hurrican.model.cc.command.ServiceChain'
    AND REF_ID = 36;
UPDATE T_SERVICE_COMMAND_MAPPING SET ORDER_NO = 20
    WHERE COMMAND_ID = (select c.ID from T_SERVICE_COMMANDS c where c.NAME='verl.check.billing.order.exist')
    AND REF_CLASS = 'de.augustakom.hurrican.model.cc.command.ServiceChain'
    AND REF_ID = 36;
UPDATE T_SERVICE_COMMAND_MAPPING SET ORDER_NO = 30
    WHERE COMMAND_ID = (select c.ID from T_SERVICE_COMMANDS c where c.NAME='verl.check.bill.channel')
    AND REF_CLASS = 'de.augustakom.hurrican.model.cc.command.ServiceChain'
    AND REF_ID = 36;
UPDATE T_SERVICE_COMMAND_MAPPING SET ORDER_NO = 40
    WHERE COMMAND_ID = (select c.ID from T_SERVICE_COMMANDS c where c.NAME='verl.check.real.date.short.term')
    AND REF_CLASS = 'de.augustakom.hurrican.model.cc.command.ServiceChain'
    AND REF_ID = 36;
UPDATE T_SERVICE_COMMAND_MAPPING SET ORDER_NO = 50
    WHERE COMMAND_ID = (select c.ID from T_SERVICE_COMMANDS c where c.NAME='verl.check.mvs.enterprise')
    AND REF_CLASS = 'de.augustakom.hurrican.model.cc.command.ServiceChain'
    AND REF_ID = 36;