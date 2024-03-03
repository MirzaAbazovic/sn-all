-- Lösche Rechte aus GUICOMPONENT, da die Klassen gelöscht wurden.

DELETE FROM   COMPBEHAVIOR
      WHERE   comp_id IN
                    (SELECT   gc.id
                       FROM   GUICOMPONENT gc JOIN COMPBEHAVIOR cb
                                 ON GC.ID = CB.COMP_ID
                      WHERE   GC.PARENT =
                                 'de.augustakom.hurrican.gui.erechnung.ERechnungHistoryDialog')
/

DELETE FROM   GUICOMPONENT
      WHERE   parent =
                 'de.augustakom.hurrican.gui.erechnung.ERechnungHistoryDialog'
/
