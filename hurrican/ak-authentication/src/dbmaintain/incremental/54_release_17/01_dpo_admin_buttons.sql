-- ANF-217.01 Hardware Administration anpassen
INSERT INTO GUICOMPONENT
(ID, NAME, PARENT,
 TYPE, APP_ID, VERSION, DESCRIPTION)
VALUES
  (S_GUICOMPONENT_0.nextval, 'dpo.init', 'de.augustakom.hurrican.gui.hvt.HwDpoAdminPanel',
   'Button', 1, 0, 'Button zum Initialiasieren einer DPO');

INSERT INTO GUICOMPONENT
(ID, NAME, PARENT,
 TYPE, APP_ID, VERSION, DESCRIPTION)
VALUES
  (S_GUICOMPONENT_0.nextval, 'dpo.modify', 'de.augustakom.hurrican.gui.hvt.HwDpoAdminPanel',
   'Button', 1, 0, 'Button zum Aktualisieren einer DPO');

INSERT INTO GUICOMPONENT
(ID, NAME, PARENT,
 TYPE, APP_ID, VERSION, DESCRIPTION)
VALUES
  (S_GUICOMPONENT_0.nextval, 'dpo.delete', 'de.augustakom.hurrican.gui.hvt.HwDpoAdminPanel',
   'Button', 1, 0, 'Button zum Löschen einer DPO');

