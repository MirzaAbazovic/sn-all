
Insert into T_GUI_DEFINITION
   (ID, CLASS, TYPE, NAME, TEXT,
    TOOLTIP, ICON, ORDER_NO, ADD_SEPARATOR, ACTIVE,
    VERSION)
 Values
   (329, 'de.augustakom.hurrican.gui.auftrag.actions.CreateBAForZentraleDispoAction',
   'ACTION', 'ba.zentraledispo.erstellen', 'Bauauftrag an zentrale Dispo',
    'Erstellt einen Bauauftrag für die zentrale Dispo', NULL, 19, NULL, '1',
    0);

Insert into T_GUI_MAPPING
   (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT, VERSION)
 Values
   (S_T_GUI_MAPPING_0.nextVal, 329, 201, 'de.augustakom.hurrican.model.cc.gui.GUIDefinition', 0);

update T_GUI_DEFINITION set TEXT='Projektierung an zentrale Dispo' where ID=323;

update T_GUI_DEFINITION set ORDER_NO=10 where ID=329;
