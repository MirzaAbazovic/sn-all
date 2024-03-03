
insert into T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ADD_SEPARATOR, ACTIVE)
values (328, 'de.augustakom.hurrican.gui.auftrag.actions.OpenExportCommandDialogAction', 'ACTION', 'open.command.export.dlg.action', 'Rangierdaten exportieren...', 'Oeffnet einen Dialog, um manuell die Rangierdaten nach Command übertragen zu koennen', null, 60, 0, 1);

insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 328, 511, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 328, 512, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 328, 513, 'de.augustakom.hurrican.model.cc.Produkt');
