
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, r.id, '1', '1', g.ID
    FROM GUICOMPONENT g, ROLE r
    WHERE g.name IN ('print', 'bemerkungen')
    AND g.parent='de.augustakom.hurrican.gui.verlauf.BauauftragMQueuePanel'
    AND r.name='verlauf.view';
