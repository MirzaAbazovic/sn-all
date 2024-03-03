delete from t_service_command_mapping 
where command_id in (
    select id 
    from t_service_commands 
    where class = 'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckAndFinishActiveLockCommand'
);

delete from t_service_commands
where class = 'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckAndFinishActiveLockCommand'
;
