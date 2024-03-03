
Insert into T_GUI_DEFINITION
   (ID, CLASS, TYPE, NAME, TEXT, 
    TOOLTIP, ORDER_NO, ACTIVE, VERSION)
 Values
   (120, 'de.augustakom.hurrican.gui.customer.actions.OpenTransponderGroupsAction', 'ACTION', 'open.transponder.groups.action', 'Transponder-Gruppen...', 
    'Öffnet ein Frame zur Verwaltung der Transponder-Gruppen', 10, '1', 0);

Insert into T_GUI_MAPPING
   (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT, VERSION)
 Values
   (70, 120, 100, 'de.augustakom.hurrican.model.cc.gui.GUIDefinition', 0);

grant delete on t_transponder to r_hurrican_user;
grant delete on t_transponder_group to r_hurrican_user;