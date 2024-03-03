--
-- Migration KuP / Hurrican
-- ===========================================================================
-- Inhalt:
--   - Anlage von notwendigen Reference-Eintraegen
--

insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE) 
  values (11004, 'STANDORT_TYP', 'GEWOFAG', '1');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE) 
  values (11005, 'STANDORT_TYP', 'ISIS', '1');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE) 
  values (11006, 'STANDORT_TYP', 'FTTH', '1');
commit;

