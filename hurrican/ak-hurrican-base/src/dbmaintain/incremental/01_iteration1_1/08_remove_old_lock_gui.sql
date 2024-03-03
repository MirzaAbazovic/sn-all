--
-- SQL-Script entfernt die alten GUI-Elemente fuer die Sperren.
--

delete from T_GUI_MAPPING where GUI_ID in (103);
delete from T_GUI_DEFINITION where ID in (103);
commit;

-- Sperr-History Action auf neue Action 'umbiegen'
update T_GUI_DEFINITION set CLASS='de.augustakom.hurrican.gui.lock.actions.ShowLockHistory4CustomerAction',
    NAME='lock.history.customer', TEXT='Sperr-Historie' where ID=102;

-- neuen Menu-Eintrag fuer Auftraege fuer Anzeige von Sperr-History
insert into T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ADD_SEPARATOR, ACTIVE)
  values (220, 'de.augustakom.hurrican.gui.lock.actions.ShowLockHistory4OrderAction',
  'ACTION', 'lock.history.order', 'Sperr-Historie', 'Zeigt die Sperr-Historie des Auftrags an',
  'de/augustakom/hurrican/gui/images/locked.gif', 91, '0', '1');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
  values (10, 220, 201, 'de.augustakom.hurrican.model.cc.gui.GUIDefinition');
commit;
