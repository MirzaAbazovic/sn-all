-- Neue Niederlassung Kreis 'Main-Kinzig'
insert into T_NIEDERLASSUNG
  (ID, TEXT, PARENT, DISPO_TEAMPOSTFACH, DISPO_PHONE,
   AREA_NO, CPS_PROVISIONING_NAME, VERSION, IP_LOCATION)
values
  (7, 'Landshut', 3, 'Supportleitstelle@m-net.de', NULL,
   10, 'Muenchen', 0, 22400);