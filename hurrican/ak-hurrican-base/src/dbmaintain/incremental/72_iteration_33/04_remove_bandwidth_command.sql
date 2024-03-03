delete from t_service_command_mapping where command_id in (
  select s.id from t_service_commands s where s.name='assign.dslamprofile.4bandwidth'
);

delete from t_service_commands where name='assign.dslamprofile.4bandwidth';