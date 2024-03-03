-- Endstellen Panel/Button 'Standorttyp ändern' für Rolle 'auftrag.bearbeiter' freigeben
INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'btn.change.location.type', 'de.augustakom.hurrican.gui.auftrag.AuftragEndstellenPanel', 'Button',
    'Abstrakte Standorte der Endstelle zuordnen', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'btn.change.location.type' AND parent = 'de.augustakom.hurrican.gui.auftrag.AuftragEndstellenPanel';
