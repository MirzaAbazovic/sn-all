Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,  APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'open.projektierung.universal.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame',
   'MenuItem', 'MenuItem für die Anzeige der abteilungs-übergreifenden Projektierungs-Maske.',  1, 0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='open.projektierung.universal.action'), 20, '1', '1', 0);


-- Button: print
Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,  APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'print', 'de.augustakom.hurrican.gui.verlauf.ProjektierungUniversalPanel', 'Button', '',  1, 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='print' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.ProjektierungUniversalPanel'),
    20, '1', '1', 0);


-- Button: ports
Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,  APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'show.ports', 'de.augustakom.hurrican.gui.verlauf.ProjektierungUniversalPanel', 'Button', '',  1, 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='show.ports' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.ProjektierungUniversalPanel'),
    20, '1', '1', 0);


-- Button: bemerkungen
Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,  APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'bemerkungen', 'de.augustakom.hurrican.gui.verlauf.ProjektierungUniversalPanel', 'Button', '',  1, 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='bemerkungen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.ProjektierungUniversalPanel'),
    20, '1', '1', 0);



-- Button: uebernehmen
Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,  APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'uebernehmen', 'de.augustakom.hurrican.gui.verlauf.ProjektierungUniversalPanel', 'Button', '',  1, 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='uebernehmen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.ProjektierungUniversalPanel'),
    28, '1', '1', 0);


-- Button: abschliessen
Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,  APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'erledigen', 'de.augustakom.hurrican.gui.verlauf.ProjektierungUniversalPanel', 'Button', '',  1, 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='erledigen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.ProjektierungUniversalPanel'),
    28, '1', '1', 0);

