
delete from t_gui_mapping where gui_id=(select id from t_gui_definition where class='de.augustakom.hurrican.gui.auftrag.actions.AbgleichEGs4KundeAction');
delete from t_gui_definition where class='de.augustakom.hurrican.gui.auftrag.actions.AbgleichEGs4KundeAction';
