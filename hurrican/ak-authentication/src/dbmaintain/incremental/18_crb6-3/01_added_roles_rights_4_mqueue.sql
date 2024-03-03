-- BauauftragMQueuePanel

-- Role
INSERT INTO ROLE (ID, NAME, DESCRIPTION, APP_ID, IS_ADMIN, IS_PARENT, VERSION)
    VALUES (S_ROLE_0.nextval, 'verlauf.mqueue', 'Rollenrechte: <br>
 - Bauaufträge von M-Queue bearbeiten', 1, 0, NULL, 0);


-- GUI-Components
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
    VALUES   (S_GUICOMPONENT_0.nextval, 'print', 'de.augustakom.hurrican.gui.verlauf.BauauftragMQueuePanel', 'Button'
     , 'Button, um einen Bauauftrag
zu drucken (M-Queue).', 1, 0);

INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
    VALUES   (S_GUICOMPONENT_0.nextval, 'ba.erledigen', 'de.augustakom.hurrican.gui.verlauf.BauauftragMQueuePanel', 'Button'
        , 'Button, um einen Bauauftrag
auf <erledigt> zu setzen (M-Queue).', 1, 0);

INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
    VALUES   (S_GUICOMPONENT_0.nextval, 'bemerkungen', 'de.augustakom.hurrican.gui.verlauf.BauauftragMQueuePanel', 'Button'
        , 'Button, um die Bemerkungen
zu einem Bauauftrag
anzuzeigen (aus M-Queue).', 1, 0);

INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
    VALUES   (S_GUICOMPONENT_0.nextval, 'show.cps.tx.history', 'de.augustakom.hurrican.gui.verlauf.BauauftragMQueuePanel'
        , 'Button', 'CPS-Tx History fuer M-Queue', 1, 0);

INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
    VALUES   (S_GUICOMPONENT_0.nextval, 'create.cps.tx', 'de.augustakom.hurrican.gui.verlauf.BauauftragMQueuePanel'
     , 'Button', 'CPS-Tx erstellen fuer M-Queue', 1, 0);


-- GUI-Component Behaviour
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, r.id, '1', '1', g.ID
    FROM GUICOMPONENT g, ROLE r
    WHERE g.parent='de.augustakom.hurrican.gui.verlauf.BauauftragMQueuePanel'
    AND r.name='verlauf.mqueue';
