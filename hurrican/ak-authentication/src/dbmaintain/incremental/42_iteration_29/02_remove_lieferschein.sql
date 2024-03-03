
DELETE FROM COMPBEHAVIOR
      WHERE COMP_ID in
               (SELECT ID FROM GUICOMPONENT
                     WHERE parent = 'de.augustakom.hurrican.gui.auftrag.eg.versand.LieferscheinTablePanel');
DELETE FROM GUICOMPONENT
      WHERE PARENT = 'de.augustakom.hurrican.gui.auftrag.eg.versand.LieferscheinTablePanel';

      
DELETE FROM COMPBEHAVIOR
      WHERE COMP_ID in
               (SELECT ID FROM GUICOMPONENT
                     WHERE parent = 'de.augustakom.hurrican.gui.auftrag.eg.versand.LieferscheinEingabePanel');
DELETE FROM GUICOMPONENT
      WHERE PARENT = 'de.augustakom.hurrican.gui.auftrag.eg.versand.LieferscheinEingabePanel';
      
      
DELETE FROM COMPBEHAVIOR
      WHERE COMP_ID in
               (SELECT ID FROM GUICOMPONENT
                     WHERE parent = 'de.augustakom.hurrican.gui.auftrag.Endgeraet2AuftragDialog');
DELETE FROM GUICOMPONENT
      WHERE PARENT = 'de.augustakom.hurrican.gui.auftrag.Endgeraet2AuftragDialog';
      
      
DELETE FROM COMPBEHAVIOR
      WHERE COMP_ID =
               (SELECT ID FROM GUICOMPONENT
                     WHERE name = 'iad.zuordnen');
DELETE FROM GUICOMPONENT
      WHERE name = 'iad.zuordnen';
      
