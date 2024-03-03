--
-- Migration KuP / Hurrican
-- ===========================================================================
-- Inhalt:
--   - SQL-Script aendert die Tabelle T_HVT_GRUPPE
--

-- Feld HAUS_NR von VARCHAR2(5) auf VARCHAR2(10)
alter table T_HVT_GRUPPE modify HAUS_NR VARCHAR2(10);
