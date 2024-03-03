delete from t_gui_mapping where gui_id in
  (select gd.id from t_gui_definition gd where gd.class like '%OpenRechercheFrameAction');
delete from t_gui_definition gd where gd.class like '%OpenRechercheFrameAction';

alter table T_REPORT drop column ARCHIV_OBJECT_ID;

drop table T_ARCHIV_PARAMETER_2_OBJECT;
drop table T_ARCHIV_POSSIBLE_PARAMETER;
drop table T_ARCHIV_OBJECT;
drop table T_ARCHIV_AUSWAHL;

drop sequence S_T_ARCHIV_OBJECT_0;
drop sequence S_T_ARCHIV_PARAMETER_2_OB_0;
drop sequence S_T_ARCHIV_POSSIBLE_PARAM_0;

