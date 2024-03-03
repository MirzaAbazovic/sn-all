--
-- Script, das die Alcatel Technik und ISIS und GEWOFAQ Standort-Typen hinzufuegt
--

insert into T_HVT_TECHNIK( ID, HERSTELLER, NAME)
values (6, 'Alcatel', 'Alcatel');

insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (11009, 'STANDORT_TYP', 'ISIS', '1');

insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (11010, 'STANDORT_TYP', 'GEWOFAG', '1');

commit;
