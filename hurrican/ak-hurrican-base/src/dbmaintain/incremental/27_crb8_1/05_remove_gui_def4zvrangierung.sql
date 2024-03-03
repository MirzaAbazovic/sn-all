delete from T_GUI_MAPPING where GUI_ID in
    (select ID from T_GUI_DEFINITION where CLASS = 'de.augustakom.hurrican.gui.auftrag.actions.OpenExportCommandDialogAction');
delete from T_GUI_DEFINITION where (CLASS = 'de.augustakom.hurrican.gui.auftrag.actions.OpenExportCommandDialogAction');
