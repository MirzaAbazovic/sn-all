Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,
    APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'split.verlauf', 'de.augustakom.hurrican.gui.verlauf.ProjektierungDispoPanel', NULL, NULL,
    1, 1);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('split.verlauf')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungDispoPanel'
    AND r.NAME IN ('verlauf.dispo') AND r.APP_ID = 1;


Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,
    APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'split.verlauf', 'de.augustakom.hurrican.gui.verlauf.ProjektierungDispoPanel.NP', NULL, NULL,
    1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('split.verlauf')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungDispoPanel.NP'
    AND r.NAME IN ('verlauf.np') AND r.APP_ID = 1;


Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,
    APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'split.verlauf', 'de.augustakom.hurrican.gui.verlauf.ProjektierungStConnectPanel', NULL, NULL,
    1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('split.verlauf')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungStConnectPanel'
    AND r.NAME IN ('verlauf.stconnect') AND r.APP_ID = 1;


Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,
    APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'split.verlauf', 'de.augustakom.hurrican.gui.verlauf.ProjektierungStOnlinePanel', NULL, NULL,
    1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('split.verlauf')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungStOnlinePanel'
    AND r.NAME IN ('verlauf.stonline') AND r.APP_ID = 1;

Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,
    APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'split.verlauf', 'de.augustakom.hurrican.gui.verlauf.ProjektierungStVoicePanel', NULL, NULL,
    1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('split.verlauf')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungStVoicePanel'
    AND r.NAME IN ('verlauf.stvoice') AND r.APP_ID = 1;


Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,
    APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'split.verlauf', 'de.augustakom.hurrican.gui.verlauf.ProjektierungFieldServicePanel', NULL, NULL,
    1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('split.verlauf')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungFieldServicePanel'
    AND r.NAME IN ('verlauf.fieldservice') AND r.APP_ID = 1;


Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,
    APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'split.verlauf', 'de.augustakom.hurrican.gui.verlauf.ProjektierungDispoRLPanel', NULL, NULL,
    1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('split.verlauf')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungDispoRLPanel'
    AND r.NAME IN ('verlauf.dispo') AND r.APP_ID = 1;


Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION,
    APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'split.verlauf', 'de.augustakom.hurrican.gui.verlauf.ProjektierungDispoRLPanel.NP', NULL, NULL,
    1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('split.verlauf')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungDispoRLPanel.NP'
    AND r.NAME IN ('verlauf.np') AND r.APP_ID = 1;


