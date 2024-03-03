-- Lösche Daten aus GUI-Definition, da die Klassen gelöscht wurden.

DELETE FROM   t_gui_definition gd
      WHERE   GD.CLASS =
                 'de.augustakom.hurrican.gui.auftrag.actions.PrintERechnungAction'
/

DELETE FROM   t_gui_mapping
      WHERE   gui_id IN
                    (SELECT   gd.id
                       FROM   t_gui_mapping gm, t_gui_definition gd
                      WHERE   GM.GUI_ID = GD.ID
                              AND gd.class =
                                    'de.augustakom.hurrican.gui.erechnung.actions.ERechnungHistoryAction')
/

DELETE FROM   t_gui_definition gd
      WHERE   GD.CLASS =
                 'de.augustakom.hurrican.gui.erechnung.actions.ERechnungHistoryAction'
/


