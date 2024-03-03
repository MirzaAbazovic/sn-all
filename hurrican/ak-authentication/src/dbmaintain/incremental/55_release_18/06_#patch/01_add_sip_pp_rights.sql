-- Bitte beachten: Daten sind teilweise manuell auf Test bereits konfiguriert -> 'weiche' Inserts verwenden!
-- Top Menu 'Administration' zusaetzliches Recht zuweisen
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  SELECT S_COMPBEHAVIOR_0.nextVal, (SELECT r.id FROM ROLE r WHERE r.name = 'sip.peering.partner'), '1', '1',
    (SELECT g.ID FROM GUICOMPONENT g WHERE g.name='menu.administration'
      AND g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame' AND g.type = 'Menu') FROM dual
  WHERE NOT EXISTS(SELECT * FROM COMPBEHAVIOR WHERE ROLE_ID = (SELECT r.id FROM ROLE r WHERE r.name = 'sip.peering.partner')
    AND COMP_ID = (SELECT g.id FROM GUICOMPONENT g WHERE g.name='menu.administration'
    AND g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame' AND g.type = 'Menu'));

-- -----------------------------
-- Save Button im NewIpSetDialog
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
    SELECT S_GUICOMPONENT_0.nextVal, 'save', 'de.augustakom.hurrican.gui.sip.peering.NewIpSetDialog', 'Button',
      'Neues SIP-Peering-Partner IP-Set speichern', 1, 0 FROM dual WHERE NOT EXISTS (SELECT * FROM GUICOMPONENT WHERE name = 'save'
        AND parent = 'de.augustakom.hurrican.gui.sip.peering.NewIpSetDialog' and type = 'Button');

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  SELECT S_COMPBEHAVIOR_0.nextVal, (SELECT r.id FROM ROLE r WHERE r.name = 'sip.peering.partner'), '1', '1',
    (SELECT g.ID FROM GUICOMPONENT g WHERE g.name='save'
      AND g.parent='de.augustakom.hurrican.gui.sip.peering.NewIpSetDialog' AND g.type = 'Button') FROM dual
  WHERE NOT EXISTS(SELECT * FROM COMPBEHAVIOR WHERE ROLE_ID = (SELECT r.id FROM ROLE r WHERE r.name = 'sip.peering.partner')
    AND COMP_ID = (SELECT g.id FROM GUICOMPONENT g WHERE g.name='save'
    AND g.parent='de.augustakom.hurrican.gui.sip.peering.NewIpSetDialog' AND g.type = 'Button'));

-- -----------------------------------
-- Save Button im IpAddressInputDialog
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
    SELECT S_GUICOMPONENT_0.nextVal, 'save', 'de.augustakom.hurrican.gui.auftrag.IpAddressInputDialog', 'Button',
      'Neue SIP-Peering-Partner IP-Adresse speichern', 1, 0 FROM dual WHERE NOT EXISTS (SELECT * FROM GUICOMPONENT WHERE name = 'save'
        AND parent = 'de.augustakom.hurrican.gui.auftrag.IpAddressInputDialog' and type = 'Button');

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  SELECT S_COMPBEHAVIOR_0.nextVal, (SELECT r.id FROM ROLE r WHERE r.name = 'sip.peering.partner'), '1', '1',
    (SELECT g.ID FROM GUICOMPONENT g WHERE g.name='save'
      AND g.parent='de.augustakom.hurrican.gui.auftrag.IpAddressInputDialog' AND g.type = 'Button') FROM dual
  WHERE NOT EXISTS(SELECT * FROM COMPBEHAVIOR WHERE ROLE_ID = (SELECT r.id FROM ROLE r WHERE r.name = 'sip.peering.partner')
    AND COMP_ID = (SELECT g.id FROM GUICOMPONENT g WHERE g.name='save'
    AND g.parent='de.augustakom.hurrican.gui.auftrag.IpAddressInputDialog' AND g.type = 'Button'));
