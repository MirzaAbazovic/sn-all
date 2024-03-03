-- HUR-23568
update USERS u set u.MANAGER = 1 WHERE u.LOGINNAME in ('Roessner', 'Iselt', 'Freudenblum', 'WirthHe');
