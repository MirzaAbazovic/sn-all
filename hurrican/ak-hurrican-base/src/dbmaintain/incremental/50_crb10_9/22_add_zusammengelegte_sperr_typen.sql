-- IMS Sperrklasse 4 und 8: PRD & Innovative Dienste sowie Offline & Auskunftsdienste sind zusammengelegt
UPDATE T_SPERRKLASSEN SET INNOVATIVE_DIENSTE='1', OFF_LINE='0' WHERE SPERRKLASSE_IMS in (4, 8);
