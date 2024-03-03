--
-- SQL-Script, um eine Spalte fuer die "alte" Bezeichnung der DSLAMs anzulegen.
--
ALTER TABLE T_HW_RACK
 ADD (MANAGEMENTBEZ  VARCHAR2(50 BYTE));
