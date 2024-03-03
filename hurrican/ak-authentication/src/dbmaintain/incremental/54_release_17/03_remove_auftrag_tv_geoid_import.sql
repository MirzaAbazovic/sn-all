DELETE FROM COMPBEHAVIOR
WHERE comp_id =
      (SELECT ID
       FROM GUICOMPONENT
       WHERE name = 'import.auftragTv2GeoId.action'
             AND parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame');

DELETE FROM GUICOMPONENT
WHERE name = 'import.auftragTv2GeoId.action';
