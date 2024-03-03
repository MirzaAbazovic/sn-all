Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,
    APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'split.verlauf', 'de.augustakom.hurrican.gui.verlauf.BauauftragDISPOPanel', NULL, NULL,
    1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('split.verlauf')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragDISPOPanel'
    AND r.NAME IN ('verlauf.dispo') AND r.APP_ID = 1;


Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,
    APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'split.verlauf', 'de.augustakom.hurrican.gui.verlauf.BauauftragDISPOPanel.NP', NULL, NULL,
    1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('split.verlauf')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragDISPOPanel.NP'
    AND r.NAME IN ('verlauf.np') AND r.APP_ID = 1;



Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,
    APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'split.verlauf', 'de.augustakom.hurrican.gui.verlauf.BauauftragStConnectPanel', NULL, NULL,
    1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('split.verlauf')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragStConnectPanel'
    AND r.NAME IN ('verlauf.stconnect') AND r.APP_ID = 1;



Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,
    APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'split.verlauf', 'de.augustakom.hurrican.gui.verlauf.BauauftragStOnlinePanel', NULL, NULL,
    1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('split.verlauf')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragStOnlinePanel'
    AND r.NAME IN ('verlauf.stonline') AND r.APP_ID = 1;


Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,
    APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'split.verlauf', 'de.augustakom.hurrican.gui.verlauf.BauauftragStVoicePanel', NULL, NULL,
    1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('split.verlauf')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragStVoicePanel'
    AND r.NAME IN ('verlauf.stvoice') AND r.APP_ID = 1;


Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,
    APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'split.verlauf', 'de.augustakom.hurrican.gui.verlauf.BauauftragFieldServicePanel', NULL, NULL,
    1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('split.verlauf')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragFieldServicePanel'
    AND r.NAME IN ('verlauf.fieldservice') AND r.APP_ID = 1;



Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,
    APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'split.verlauf', 'de.augustakom.hurrican.gui.verlauf.BauauftragMQueuePanel', NULL, NULL,
    1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('split.verlauf')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragMQueuePanel'
    AND r.NAME IN ('verlauf.mqueue') AND r.APP_ID = 1;


Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,
    APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'split.verlauf', 'de.augustakom.hurrican.gui.verlauf.BauauftragDispoRLPanel', NULL, NULL,
    1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('split.verlauf')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragDispoRLPanel'
    AND r.NAME IN ('verlauf.dispo') AND r.APP_ID = 1;



Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,
    APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'split.verlauf', 'de.augustakom.hurrican.gui.verlauf.BauauftragDispoRLPanel.NP', NULL, NULL,
    1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('split.verlauf')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragDispoRLPanel.NP'
    AND r.NAME IN ('verlauf.np') AND r.APP_ID = 1;


