Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,  APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'open.ba.verlauf.universal.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame',
   'MenuItem', 'MenuItem für die Anzeige der abteilungs-übergreifenden Bauauftrags-Maske.',  1, 0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='open.ba.verlauf.universal.action'), 20, '1', '1', 0);


-- Button: print
Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,  APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'print', 'de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel', 'Button', '',  1, 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='print' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    20, '1', '1', 0);


-- Button: print.compact
Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,  APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'print.compact', 'de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel', 'Button', '',  1, 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='print.compact' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    20, '1', '1', 0);



-- Button: ba.erledigen
Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,  APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'ba.erledigen', 'de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel', 'Button', '',  1, 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='ba.erledigen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    28, '1', '1', 0);



-- Button: show.ports
Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,  APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'show.ports', 'de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel', 'Button', '',  1, 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='show.ports' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    28, '1', '1', 0);



-- Button: bemerkungen
Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,  APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'bemerkungen', 'de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel', 'Button', '',  1, 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='bemerkungen' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    28, '1', '1', 0);



-- Button: uebernahme
Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,  APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'uebernahme', 'de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel', 'Button', '',  1, 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='uebernahme' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    28, '1', '1', 0);



-- Button: show.cps.tx.history
Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,  APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'show.cps.tx.history', 'de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel', 'Button', '',  1, 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='show.cps.tx.history' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    28, '1', '1', 0);



-- Button: create.cps.tx
Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,  APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'create.cps.tx', 'de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel', 'Button', '',  1, 0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
    (select gc.ID from GUICOMPONENT gc where gc.NAME='create.cps.tx' and gc.PARENT='de.augustakom.hurrican.gui.verlauf.BauauftragUniversalPanel'),
    28, '1', '1', 0);

