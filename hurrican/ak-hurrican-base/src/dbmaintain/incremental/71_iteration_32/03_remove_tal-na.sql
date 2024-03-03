delete from t_gui_mapping where gui_id in
  (select id from t_gui_definition where class='de.augustakom.hurrican.gui.auftrag.actions.PrintTalNAAction');
delete from t_gui_definition where class='de.augustakom.hurrican.gui.auftrag.actions.PrintTalNAAction';

delete from t_gui_mapping where gui_id in
  (select id from t_gui_definition where class='de.augustakom.hurrican.gui.auftrag.actions.PrintOnlineAction');
delete from t_gui_definition where class='de.augustakom.hurrican.gui.auftrag.actions.PrintOnlineAction';

