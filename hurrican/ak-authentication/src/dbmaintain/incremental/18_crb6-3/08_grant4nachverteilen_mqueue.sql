INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
    VALUES (S_GUICOMPONENT_0.nextval, 'ba.nachverteilen'
        , 'de.augustakom.hurrican.gui.verlauf.BauauftragMQueuePanel', 'MenuItem', 'MenuItem, um einen
Bauauftrag von
M-Queue nachzuverteilen.', 1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, r.id, '1', '1', g.ID
    FROM GUICOMPONENT g, ROLE r
    WHERE g.name = 'ba.nachverteilen'
    AND r.name='verlauf.mqueue';
