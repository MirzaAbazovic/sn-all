-- BauauftragMQueuePanel

-- GUI-Components
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
    VALUES   (S_GUICOMPONENT_0.nextval, 'uebernahme', 'de.augustakom.hurrican.gui.verlauf.BauauftragMQueuePanel', 'Button'
     , 'Button, um einen Bauauftrag
zu übernehmen (M-Queue).', 1, 0);

-- GUI-Component Behaviour
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, r.id, '1', '1', g.ID
    FROM GUICOMPONENT g, ROLE r
    WHERE g.name = 'uebernahme' AND g.parent = 'de.augustakom.hurrican.gui.verlauf.BauauftragMQueuePanel'
    AND r.name='verlauf.mqueue';
