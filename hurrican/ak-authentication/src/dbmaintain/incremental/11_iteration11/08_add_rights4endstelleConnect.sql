-- Rechte f�r Hurrican einf�gen
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'connect.gebaeude', 'de.augustakom.hurrican.gui.auftrag.EndstelleConnectPanel',
    'TextField', 'Textfeld f�r Geb�ude der Connect-Daten einer Endstelle', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'connect.etage', 'de.augustakom.hurrican.gui.auftrag.EndstelleConnectPanel',
    'TextField', 'Textfeld f�r Etage der Connect-Daten einer Endstelle', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'connect.raum', 'de.augustakom.hurrican.gui.auftrag.EndstelleConnectPanel',
    'TextField', 'Textfeld f�r Raum der Connect-Daten einer Endstelle', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'connect.schrank', 'de.augustakom.hurrican.gui.auftrag.EndstelleConnectPanel',
    'TextField', 'Textfeld f�r Schrank der Connect-Daten einer Endstelle', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'connect.uebergabe', 'de.augustakom.hurrican.gui.auftrag.EndstelleConnectPanel',
    'TextField', 'Textfeld f�r Endger�te-�bergabe der Connect-Daten einer Endstelle', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'connect.bandbreite', 'de.augustakom.hurrican.gui.auftrag.EndstelleConnectPanel',
    'TextField', 'Textfeld f�r Bandbreite der Connect-Daten einer Endstelle', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'connect.if', 'de.augustakom.hurrican.gui.auftrag.EndstelleConnectPanel',
    'ComboBox', 'Combobox f�r Schnittstelle der Connect-Daten einer Endstelle', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'connect.if-einstellung', 'de.augustakom.hurrican.gui.auftrag.EndstelleConnectPanel',
    'ComboBox', 'Combobox f�r Einstellung der Schnittstelle der Connect-Daten einer Endstelle', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'connect.routerinfo', 'de.augustakom.hurrican.gui.auftrag.EndstelleConnectPanel',
    'ComboBox', 'Combobox f�r Routerinfo der Connect-Daten einer Endstelle', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'connect.routertyp', 'de.augustakom.hurrican.gui.auftrag.EndstelleConnectPanel',
    'TextField', 'Textfeld f�r Routertyp der Connect-Daten einer Endstelle', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'connect.bemerkung', 'de.augustakom.hurrican.gui.auftrag.EndstelleConnectPanel',
    'TextArea', 'Textbereich f�r Bemerkung der Connect-Daten einer Endstelle', 1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
    FROM GUICOMPONENT g, ROLE r
    WHERE g.NAME IN ('connect.gebaeude', 'connect.etage', 'connect.raum', 'connect.schrank'
            , 'connect.uebergabe', 'connect.bandbreite', 'connect.if', 'connect.if-einstellung'
            , 'connect.routerinfo', 'connect.routertyp', 'connect.bemerkung')
        AND g.PARENT = 'de.augustakom.hurrican.gui.auftrag.EndstelleConnectPanel'
        AND r.NAME = 'HURRICAN_ST_CON_DEFAULT';
