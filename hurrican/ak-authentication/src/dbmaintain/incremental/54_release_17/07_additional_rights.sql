-- abteilungs-uebergreifende BA/Projektierungs GUI: weitere Rollen fuer schreibende Aktionen

-- ba.erledigen
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='ba.erledigen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    22, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='ba.erledigen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    25, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='ba.erledigen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    26, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='ba.erledigen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    27, '1', '1', 0);

-- bemerkungen
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='bemerkungen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    22, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='bemerkungen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    25, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='bemerkungen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    26, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='bemerkungen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    27, '1', '1', 0);


-- show.ports
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='show.ports' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    22, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='show.ports' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    25, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='show.ports' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    26, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='show.ports' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    27, '1', '1', 0);


-- uebernahme
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values  (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='uebernahme' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    22, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values  (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='uebernahme' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    25, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values  (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='uebernahme' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    26, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values  (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='uebernahme' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    27, '1', '1', 0);


-- cps.tx.history
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='show.cps.tx.history' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    22, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='show.cps.tx.history' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    25, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='show.cps.tx.history' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    26, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='show.cps.tx.history' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    27, '1', '1', 0);


-- create.cps
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values  (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='create.cps.tx' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    22, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values  (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='create.cps.tx' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    25, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values  (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='create.cps.tx' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    26, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values  (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='create.cps.tx' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    27, '1', '1', 0);


-- zusaetzliche Berechtigungen auf Projektierungs-GUI
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='uebernehmen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.ProjektierungUniversalPanel'),
    22, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='uebernehmen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.ProjektierungUniversalPanel'),
    25, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='uebernehmen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.ProjektierungUniversalPanel'),
    26, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='uebernehmen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.ProjektierungUniversalPanel'),
    27, '1', '1', 0);


-- Button: abschliessen
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='erledigen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.ProjektierungUniversalPanel'),
    22, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='erledigen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.ProjektierungUniversalPanel'),
    25, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='erledigen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.ProjektierungUniversalPanel'),
    26, '1', '1', 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='erledigen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.ProjektierungUniversalPanel'),
    27, '1', '1', 0);

