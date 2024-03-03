--
-- SQL-Script, um die DSLAM-Tabelle um ERX-Daten zu erweitern
--

alter table T_DSLAM add column ERX_IFACE_DATEN varchar(10) after HVT_ID_STANDORT;
alter table T_DSLAM add column ERX_STANDORT varchar(50) after ERX_IFACE_DATEN;
alter table T_DSLAM add column PHYSIK_ART varchar(10) after ERX_STANDORT;
alter table T_DSLAM add column PHYSIK_WERT varchar(10) after PHYSIK_ART;




