
delete from t_gui_mapping where GUI_ID=211;
insert into t_gui_mapping (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
  values (S_T_GUI_MAPPING_0.NEXTVAL,
          211,
          -99,
          'de.augustakom.hurrican.model.cc.ProduktGruppe');
