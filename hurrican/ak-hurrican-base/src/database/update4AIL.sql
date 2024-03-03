--
-- SQL-Script, um die Anpassungen in Hurrican für die AIL durchzuführen.
--

alter session set nls_date_format='yyyy-mm-dd';

-- Mehrfachverschlagwortung
alter table t_archiv_parameter_2_object add multiple char(1);
update t_archiv_parameter_2_object set multiple='1' where OBJECT_ID_INTERN = 6 and PARAMETER_ID_INTERN in (1,5,6,22);
update T_ARCHIV_PARAMETER_2_OBJECT set MANDATORY = 0 where ID = 36;
commit;


insert into T_ARCHIV_OBJECT (ID, OID_SV, O_BESCHREIBUNG, MATCHCODE) 
values (7, 0, 'Komplettes Archiv', 'ALL');
insert into T_ARCHIV_PARAMETER_2_OBJECT (ID, OBJECT_ID_INTERN, PARAMETER_ID_INTERN, AUSWAHL_ID, IS_DEFAULT, COMMANDCLASS, MATCHCODE, IS_APPEND_KEY, MANDATORY, MULTIPLE) 
values (38, 7, 1, 2, 0, 'de.augustakom.hurrican.service.cc.impl.command.archiv.SVGetDebitor4AuftragIdCommand', null, 0, 0, 0);
insert into T_ARCHIV_PARAMETER_2_OBJECT (ID, OBJECT_ID_INTERN, PARAMETER_ID_INTERN, AUSWAHL_ID, IS_DEFAULT, COMMANDCLASS, MATCHCODE, IS_APPEND_KEY, MANDATORY, MULTIPLE) 
values (39, 7, 3, 1, 0, 'de.augustakom.hurrican.service.cc.impl.command.archiv.SVGetKundeNoCommand', null, 0, 0, 0);
commit;