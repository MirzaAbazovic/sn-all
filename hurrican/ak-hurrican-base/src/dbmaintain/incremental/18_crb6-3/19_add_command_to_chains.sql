-- neuen Command anlegen
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
    VALUES ( 2018, 'verl.check.dn.services', 'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckAndAssignDnServicesCommand', 'VERLAUF_CHECK',
        'Command prueft ob dem Auftrag alle notwendigen Rufnummernleistungen aus Taifun zugeordnet sind', 0);

-- 'Lücke' in der Chain für den neuen Command nach CheckDNCount schaffen
UPDATE T_SERVICE_COMMAND_MAPPING m SET m.order_no = m.order_no+1
    WHERE m.ref_id IN (
        SELECT ref_id FROM T_SERVICE_COMMAND_MAPPING WHERE COMMAND_ID = 2004
    ) AND m.order_no > (
        SELECT order_no FROM T_SERVICE_COMMAND_MAPPING WHERE COMMAND_ID = 2004 AND ref_id = m.ref_id
    );

-- neuen Command nach CheckDNCount in Chains einfügen
INSERT INTO T_SERVICE_COMMAND_MAPPING
    SELECT S_T_SERVICE_COMMAND_MAPPI_0.nextval, 2018, t.*, 0
        FROM (SELECT ref_id, ref_class, order_no+1 FROM T_SERVICE_COMMAND_MAPPING WHERE COMMAND_ID = 2004) t;
