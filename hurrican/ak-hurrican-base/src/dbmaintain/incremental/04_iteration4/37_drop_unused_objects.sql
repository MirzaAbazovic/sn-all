--
-- Script entfernt nicht mehr benoetigte Tabelle/Spalten
--

drop table T_SPERRE;

alter table T_EQUIPMENT drop column DSLAM_ID;
drop table T_DSLAM;
