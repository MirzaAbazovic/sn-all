INSERT INTO GUICOMPONENT
(ID, NAME, PARENT,
 TYPE, APP_ID, VERSION, DESCRIPTION)
VALUES
  (S_GUICOMPONENT_0.nextval, 'mdu.init', 'de.augustakom.hurrican.gui.hvt.HWMduAdminPanel',
   'Button', 1, 0, 'Button zum Initialiasieren einer MDU');

INSERT INTO GUICOMPONENT
(ID, NAME, PARENT,
 TYPE, APP_ID, VERSION, DESCRIPTION)
VALUES
  (S_GUICOMPONENT_0.nextval, 'ont.init', 'de.augustakom.hurrican.gui.hvt.HwOntAdminPanel',
   'Button', 1, 0, 'Button zum Initialiasieren einer ONT');

INSERT INTO GUICOMPONENT
(ID, NAME, PARENT,
 TYPE, APP_ID, VERSION, DESCRIPTION)
VALUES
  (S_GUICOMPONENT_0.nextval, 'ont.modify', 'de.augustakom.hurrican.gui.hvt.HwOntAdminPanel',
   'Button', 1, 0, 'Button zum Aktualisieren einer ONT');

INSERT INTO GUICOMPONENT
(ID, NAME, PARENT,
 TYPE, APP_ID, VERSION, DESCRIPTION)
VALUES
  (S_GUICOMPONENT_0.nextval, 'ont.delete', 'de.augustakom.hurrican.gui.hvt.HwOntAdminPanel',
   'Button', 1, 0, 'Button zum Löschen einer ONT');
