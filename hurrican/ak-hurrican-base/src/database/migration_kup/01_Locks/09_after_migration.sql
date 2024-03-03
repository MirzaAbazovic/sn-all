--
-- SQL-Script, das nach erfolgreicher Migration der Sperrverwaltung
-- ausgefuehrt werden muss.
--

drop table T_SPERRE;
commit;
