-- BauauftragMQueuePanel

-- GUI-Components
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
    VALUES (S_GUICOMPONENT_0.nextval, 'open.ba.verlauf.mqueue.action'
        , 'de.augustakom.hurrican.gui.system.HurricanMainFrame', 'MenuItem', 'MenuItem, um die
Bauaufträge von
M-Queue zu öffnen.', 1, 0);

-- GUI-Component Behaviour
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, r.id, '1', '1', g.ID
    FROM GUICOMPONENT g, ROLE r
    WHERE g.name = 'open.ba.verlauf.mqueue.action'
    AND r.name='verlauf.view';
