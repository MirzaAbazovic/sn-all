INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'print.compact', 'de.augustakom.hurrican.gui.verlauf.BauauftragDISPOPanel',
    'Button', '', 1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('print.compact')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragDISPOPanel'
    AND r.NAME IN ('verlauf.view', 'verlauf.dispo') AND r.APP_ID = 1;


INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'print.compact', 'de.augustakom.hurrican.gui.verlauf.BauauftragStConnectPanel',
    'Button', '', 1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('print.compact')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragStConnectPanel'
    AND r.NAME IN ('verlauf.view', 'verlauf.stconnect') AND r.APP_ID = 1;


INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'print.compact', 'de.augustakom.hurrican.gui.verlauf.BauauftragStOnlinePanel',
    'Button', '', 1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('print.compact')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragStOnlinePanel'
    AND r.NAME IN ('verlauf.view', 'verlauf.stonline') AND r.APP_ID = 1;


INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'print.compact', 'de.augustakom.hurrican.gui.verlauf.BauauftragStVoicePanel',
    'Button', '', 1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('print.compact')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragStVoicePanel'
    AND r.NAME IN ('verlauf.view', 'verlauf.stvoice') AND r.APP_ID = 1;


INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'print.compact', 'de.augustakom.hurrican.gui.verlauf.BauauftragFieldServicePanel',
    'Button', '', 1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('print.compact')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragFieldServicePanel'
    AND r.NAME IN ('verlauf.view', 'verlauf.fieldservice') AND r.APP_ID = 1;


INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'print.compact', 'de.augustakom.hurrican.gui.verlauf.BauauftragEXTPanel',
    'Button', '', 1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('print.compact')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragEXTPanel'
    AND r.NAME IN ('verlauf.view', 'verlauf.dispo') AND r.APP_ID = 1;


INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'print.compact', 'de.augustakom.hurrican.gui.verlauf.BauauftragMQueuePanel',
    'Button', '', 1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('print.compact')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragMQueuePanel'
    AND r.NAME IN ('verlauf.view') AND r.APP_ID = 1;
