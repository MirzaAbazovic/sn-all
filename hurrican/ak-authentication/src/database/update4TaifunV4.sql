--
-- SQL-Script, um die AUTHENTICATION auf Taifun V4.3 umzustellen
--

update DB set URL='jdbc:oracle:thin:@mnetdbsvr06.m-net.de:1521:TAPROD01', SCHEMA='TAPROD01' where ID=3;
update DB set FALLBACK_DRIVER=null, FALLBACK_URL=null, FALLBACK_SCHEMA=null, FALLBACK_HIBERNATE_DIALECT=null where ID=3;

