--
-- SQL-Script, um neue Privileges zu setzen.
--

GRANT DELETE ON `hurrican`.`t_eg_2_auftrag` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_eg_2_auftrag` TO "hurrican-writer"@"10.10.%";
GRANT DELETE ON `hurrican`.`t_eg_2_auftrag` TO "hurrican-writer"@"10.2.%";
GRANT DELETE ON `hurrican`.`t_eg_2_auftrag` TO "hurrican-writer"@"10.20.%";
GRANT DELETE ON `hurrican`.`t_eg_2_auftrag` TO "hurrican-writer"@"10.3.%";
GRANT DELETE ON `hurrican`.`t_eg_2_auftrag` TO "hurrican-writer"@"10.30.%";

flush privileges;
