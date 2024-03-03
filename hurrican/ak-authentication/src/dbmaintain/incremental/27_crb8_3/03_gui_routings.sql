
Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'move.ip.route', 'de.augustakom.hurrican.gui.auftrag.internet.IpRoutePanel', 'MENUITEM',
    1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'move.ip.route' AND parent = 'de.augustakom.hurrican.gui.auftrag.internet.IpRoutePanel';
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 28, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'move.ip.route' AND parent = 'de.augustakom.hurrican.gui.auftrag.internet.IpRoutePanel';
