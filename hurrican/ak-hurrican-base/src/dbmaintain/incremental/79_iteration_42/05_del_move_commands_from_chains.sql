-- Da sich die Fachbereiche ueber den Umzug der IP Adresssen noch nicht einig sind, werden die Commands zunaechst entfernt, 
-- damit Hurrican life gehen kann.  
DELETE FROM t_service_command_mapping
      WHERE command_id IN (31, 32)
      AND REF_CLASS = 'de.augustakom.hurrican.model.cc.command.ServiceChain';