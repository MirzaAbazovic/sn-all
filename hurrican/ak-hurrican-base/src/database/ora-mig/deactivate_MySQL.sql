--
-- SQL-Statement, um die MySQL-DB zu sperren
--

-- Applikationsbenutzer sperren
use authentication;
update user set active=0;


-- TODO 
-- folgende Datenbanken muessen umbenannt oder entfernt werden:
--    - authentication
--    - hurrican
--    - scheduler

-- Moeglichkeit, die Datenbank 'log' in die Datenbank 'log2' zu uebertragen:
-- mysql>	create database log2;
-- c:\>		mysqldump -h 10.1.20.2 -u its -pits --opt log | mysql -h 10.1.20.2 -u its -pits log2


