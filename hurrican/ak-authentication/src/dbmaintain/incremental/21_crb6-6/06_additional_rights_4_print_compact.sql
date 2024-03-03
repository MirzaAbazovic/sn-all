INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('print.compact')
    AND g.PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragMQueuePanel'
    AND r.NAME IN ('verlauf.mqueue') AND r.APP_ID = 1;