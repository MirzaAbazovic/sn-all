-- an Position 20 Platz schaffen
UPDATE T_SERVICE_COMMAND_MAPPING SET ORDER_NO = 80 WHERE REF_ID = 125 AND ORDER_NO = 70;
UPDATE T_SERVICE_COMMAND_MAPPING SET ORDER_NO = 70 WHERE REF_ID = 125 AND ORDER_NO = 60;
UPDATE T_SERVICE_COMMAND_MAPPING SET ORDER_NO = 60 WHERE REF_ID = 125 AND ORDER_NO = 50;
UPDATE T_SERVICE_COMMAND_MAPPING SET ORDER_NO = 50 WHERE REF_ID = 125 AND ORDER_NO = 40;
UPDATE T_SERVICE_COMMAND_MAPPING SET ORDER_NO = 40 WHERE REF_ID = 125 AND ORDER_NO = 30;
UPDATE T_SERVICE_COMMAND_MAPPING SET ORDER_NO = 30 WHERE REF_ID = 125 AND ORDER_NO = 20;

-- 'verl.check.accounts' einf�gen
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2000, 125, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20, 0);
