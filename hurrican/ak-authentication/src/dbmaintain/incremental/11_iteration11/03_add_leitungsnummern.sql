-- Rechte für Hurrican einfügen
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'panel.button.new', 'de.augustakom.hurrican.gui.auftrag.LeitungsnummerPanel',
    'Button', 'Button, um eine neue Leitungsnummer hinzuzufügen', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
   (S_GUICOMPONENT_0.NEXTVAL, 'panel.button.delete', 'de.augustakom.hurrican.gui.auftrag.LeitungsnummerPanel',
   'Button', 'Button, um eine Leitungsnummer zu löschen', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
   (S_GUICOMPONENT_0.NEXTVAL, 'save', 'de.augustakom.hurrican.gui.auftrag.LeitungsnummerPanel$LeitungsnummerDialog',
   'Button', 'Button, um die Leitungsnummer zu speichern', 1, 0);

INSERT INTO ROLE (ID, NAME, DESCRIPTION, APP_ID, IS_ADMIN, IS_PARENT, VERSION) VALUES
   (S_ROLE_0.NEXTVAL, 'auftrag.bearbeiter.leitungsnummer', 'Role, die die Leitungsnummern anlegen, ändern und löschen darf',
   1, '0', NULL, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
    FROM GUICOMPONENT g, ROLE r
    WHERE g.NAME IN ('panel.button.delete', 'panel.button.new', 'save')
        AND g.PARENT LIKE 'de.augustakom.hurrican.gui.auftrag.LeitungsnummerPanel%'
        AND r.NAME = 'auftrag.bearbeiter.leitungsnummer';
