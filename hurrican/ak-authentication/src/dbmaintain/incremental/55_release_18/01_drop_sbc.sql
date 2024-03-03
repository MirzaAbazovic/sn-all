DELETE FROM COMPBEHAVIOR
WHERE comp_id =
      (SELECT ID
       FROM GUICOMPONENT
       WHERE name = 'new.sbcaddr'
             AND parent = 'de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel');

DELETE FROM GUICOMPONENT
WHERE name = 'new.sbcaddr';


DELETE FROM COMPBEHAVIOR
WHERE comp_id =
      (SELECT ID
       FROM GUICOMPONENT
       WHERE name = 'del.sbcaddr'
             AND parent = 'de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel');

DELETE FROM GUICOMPONENT
WHERE name = 'del.sbcaddr';





