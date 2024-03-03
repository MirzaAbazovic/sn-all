-- Endstellen-Panel für Site-2-Site aktivieren

update t_produkt set ENDSTELLEN_TYP = 2 where prod_id = 444;

insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
values (s_t_gui_mapping_0.nextval, 203, 444, 'de.augustakom.hurrican.model.cc.Produkt');