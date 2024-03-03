-- Neue Niederlassung Kreis 'Main-Kinzig'
insert into T_NIEDERLASSUNG
   (ID, TEXT, PARENT, DISPO_TEAMPOSTFACH, DISPO_PHONE, 
    AREA_NO, CPS_PROVISIONING_NAME, VERSION, IP_LOCATION)
 values
   (6, 'Main-Kinzig', 4, 'Supportleitstelle@m-net.de', NULL, 
    6, 'Nuernberg', 0, 22402);
