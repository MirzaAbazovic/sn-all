
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
    VALUES   (S_GUICOMPONENT_0.nextval, 'verlauf.abteilung.status.13', 'de.augustakom.hurrican.gui.verlauf.VerlaufsBemerkungenPanel'
       , 'TextField', 'Status des Bauauftrags
von M-Queue', 1, 0);

INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
    VALUES   (S_GUICOMPONENT_0.nextval, 'verlauf.bemerkung.13', 'de.augustakom.hurrican.gui.verlauf.VerlaufsBemerkungenPanel', 'TextArea'
       , 'TextArea für die
Bauauftragsbemerk.
von M-Queue',  1, 0);

INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
    VALUES   (S_GUICOMPONENT_0.nextval, 'verlauf.wiedervorlage.13', 'de.augustakom.hurrican.gui.verlauf.VerlaufsBemerkungenPanel'
       , 'DateComp', 'Wiedervorlage für den
Bauauftrag bei M-Queue', 1, 0);

-- GUI-Component Behaviour
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, r.id, '1', '1', g.ID
    FROM GUICOMPONENT g, ROLE r
    WHERE g.name IN ('verlauf.abteilung.status.13', 'verlauf.bemerkung.13', 'verlauf.wiedervorlage.13')
    AND r.name='verlauf.mqueue';
