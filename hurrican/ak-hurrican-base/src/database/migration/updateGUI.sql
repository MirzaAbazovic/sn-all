--
-- GUI-Actions entfernen
--

-- Endgeraete-Panel von AuftragsFrame entfernen
delete from t_gui_mapping where GUI_ID=211;
delete from t_gui_definition where ID=211;


