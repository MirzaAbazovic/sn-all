
DELETE FROM T_SERVICE_COMMAND_MAPPING WHERE COMMAND_ID =
    (SELECT ID FROM T_SERVICE_COMMANDS WHERE CLASS = 'de.augustakom.hurrican.service.cc.impl.command.physik.CopyAnbindungsartCommand');
DELETE FROM T_SERVICE_COMMANDS WHERE CLASS = 'de.augustakom.hurrican.service.cc.impl.command.physik.CopyAnbindungsartCommand';
