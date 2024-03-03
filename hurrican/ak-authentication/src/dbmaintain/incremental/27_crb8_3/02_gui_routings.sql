
Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'delete.ip.route', 'de.augustakom.hurrican.gui.auftrag.internet.IpRoutePanel', 'BUTTON',
    1, 0);
Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'add.ip.route', 'de.augustakom.hurrican.gui.auftrag.internet.IpRoutePanel', 'BUTTON',
    1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'delete.ip.route' AND parent = 'de.augustakom.hurrican.gui.auftrag.internet.IpRoutePanel';
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 28, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'delete.ip.route' AND parent = 'de.augustakom.hurrican.gui.auftrag.internet.IpRoutePanel';

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'add.ip.route' AND parent = 'de.augustakom.hurrican.gui.auftrag.internet.IpRoutePanel';
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 28, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'add.ip.route' AND parent = 'de.augustakom.hurrican.gui.auftrag.internet.IpRoutePanel';
