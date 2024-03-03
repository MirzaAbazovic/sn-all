--
-- SQL-Script, um die Aenderungen an Sperrklassen durchzuführen
--
alter table t_sperrklassen add column OFFLINE bit after international;
update t_sperrklassen set OFFLINE=false;

insert into t_sperrklassen (SPERRKLASSE, ABGEHEND, NATIONAL, INNOVATIVE_DIENSTE, MABEZ, MOBIL, VPN, PRD, AUSKUNFTSDIENSTE, INTERNATIONAL, OFFLINE, ANSICHT) 
values (171, false, false, true, false, false, true, true, false, false, true, 0);

insert into t_sperrklassen (SPERRKLASSE, ABGEHEND, NATIONAL, INNOVATIVE_DIENSTE, MABEZ, MOBIL, VPN, PRD, AUSKUNFTSDIENSTE, INTERNATIONAL, OFFLINE, ANSICHT) 
values (172, false, true, true, false, false, true, true, false, false, true, 0);

insert into t_sperrklassen (SPERRKLASSE, ABGEHEND, NATIONAL, INNOVATIVE_DIENSTE, MABEZ, MOBIL, VPN, PRD, AUSKUNFTSDIENSTE, INTERNATIONAL, OFFLINE, ANSICHT) 
values (173, false, false, true, true, false, true, true, false, false, true, 0);

insert into t_sperrklassen (SPERRKLASSE, ABGEHEND, NATIONAL, INNOVATIVE_DIENSTE, MABEZ, MOBIL, VPN, PRD, AUSKUNFTSDIENSTE, INTERNATIONAL, OFFLINE, ANSICHT) 
values (174, false, false, true, false, true, true, true, false, false, true, 0);

insert into t_sperrklassen (SPERRKLASSE, ABGEHEND, NATIONAL, INNOVATIVE_DIENSTE, MABEZ, MOBIL, VPN, PRD, AUSKUNFTSDIENSTE, INTERNATIONAL, OFFLINE, ANSICHT) 
values (175, false, false, true, true, true, true, true, false, false, true, 0);

insert into t_sperrklassen (SPERRKLASSE, ABGEHEND, NATIONAL, INNOVATIVE_DIENSTE, MABEZ, MOBIL, VPN, PRD, AUSKUNFTSDIENSTE, INTERNATIONAL, OFFLINE, ANSICHT) 
values (176, false, false, true, false, false, true, true, false, true, true, 0);

insert into t_sperrklassen (SPERRKLASSE, ABGEHEND, NATIONAL, INNOVATIVE_DIENSTE, MABEZ, MOBIL, VPN, PRD, AUSKUNFTSDIENSTE, INTERNATIONAL, OFFLINE, ANSICHT) 
values (177, false, false, true, true, false, true, true, false, true, true, 0);

insert into t_sperrklassen (SPERRKLASSE, ABGEHEND, NATIONAL, INNOVATIVE_DIENSTE, MABEZ, MOBIL, VPN, PRD, AUSKUNFTSDIENSTE, INTERNATIONAL, OFFLINE, ANSICHT) 
values (178, false, false, true, false, true, true, true, false, true, true, 0);
   