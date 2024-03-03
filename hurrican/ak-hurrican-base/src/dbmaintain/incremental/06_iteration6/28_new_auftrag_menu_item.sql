
Insert into T_GUI_DEFINITION
   (ID, CLASS, TYPE, NAME, TEXT,
    TOOLTIP, ICON, ORDER_NO, ADD_SEPARATOR, ACTIVE)
 Values
   (323, 'de.augustakom.hurrican.gui.auftrag.actions.CreateProjektierungForZentraleDispoAction', 'ACTION', 'projektierung.zentraledispo.erstellen', 'An zentrale Dispo weiterleiten',
    'Erstellt eine Projektierung für die zentrale Dispo', NULL, 18, NULL, '1');


Insert into T_GUI_MAPPING
   (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
 Values
   (S_T_GUI_MAPPING_0.NEXTVAL, 323, 201, 'de.augustakom.hurrican.model.cc.gui.GUIDefinition');


