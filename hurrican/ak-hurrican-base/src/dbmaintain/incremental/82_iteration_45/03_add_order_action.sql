Insert into T_GUI_DEFINITION
   (ID, CLASS, TYPE, NAME, TEXT,
    TOOLTIP, ORDER_NO, ACTIVE, VERSION)
 Values
   (106, 'de.augustakom.hurrican.gui.auftrag.actions.ShowAuftragsMonitorWithTaifunOrderSelectionAction', 'ACTION', 'show.auftragsmonitor.selective',
   'Taifun-Auftrag übernehmen (selektiv)...',
    'Listet alle Taifun-Aufträge des Kunden auf und bietet die Möglichkeit, einen Taifun-Auftrag mit Hurrican abzugleichen.',
    3, '1', 0);

Insert into T_GUI_MAPPING
   (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT, VERSION)
 Values
   (60, 106, 100, 'de.augustakom.hurrican.model.cc.gui.GUIDefinition', 0);

-- weitere Eintraege nach "unten" sortieren
update T_GUI_DEFINITION set ORDER_NO=4, ADD_SEPARATOR='1' where ID=101;
update T_GUI_DEFINITION set ORDER_NO=5 where ID=102;

