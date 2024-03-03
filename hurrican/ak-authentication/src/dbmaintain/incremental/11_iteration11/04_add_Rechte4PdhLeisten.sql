-- Rechte für Hurrican einfügen
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
   (S_GUICOMPONENT_0.NEXTVAL, 'pdhleiste.create', 'de.augustakom.hurrican.gui.base.tree.hardware.HardwareTreeFrame',
   'MenuItem', 'MenuItem, um eine PDH-Leiste anzulegen', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
   (S_GUICOMPONENT_0.NEXTVAL, 'save', 'de.augustakom.hurrican.gui.base.tree.hardware.PdhLeisteDialog',
   'Button', 'Button, um eine PDH-Leiste anzulegen', 1, 0);
