-- Rollen und Rechte fuer die GUI Komponenten der SIP Peering Partner (Admin + Auftrag)

--  Admin
INSERT INTO ROLE (ID, NAME, DESCRIPTION, APP_ID, IS_ADMIN, VERSION)
VALUES (S_ROLE_0.nextVal, 'sip.peering.partner', 'Rolle um SIP-Peering-Partner zu pflegen', 1, '0', 0);

INSERT INTO GUICOMPONENT
(ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
VALUES
  (S_GUICOMPONENT_0.nextVal, 'admin.peering.partner.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame',
   'MenuItem', 'MenuItem fuer die Administration der SIP Peering Partner.', 1, 0);

INSERT INTO COMPBEHAVIOR
(ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
VALUES
  (S_COMPBEHAVIOR_0.nextVal,
   (SELECT id
    FROM GUICOMPONENT
    WHERE name = 'admin.peering.partner.action' AND type = 'MenuItem'),
   (SELECT id
    FROM ROLE
    WHERE name = 'sip.peering.partner'), '1', '1', 0);

-- Auftrag
-- Add Button
INSERT INTO GUICOMPONENT
(ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
VALUES
  (S_GUICOMPONENT_0.nextVal, 'add.peering.partner', 'de.augustakom.hurrican.gui.auftrag.AuftragPeeringPartnerPanel',
   'Button', 'Fuegt einem Auftrag einen SIP-Peering-Partner hinzu.', 1, 0);

INSERT INTO COMPBEHAVIOR
(ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
VALUES
  (S_COMPBEHAVIOR_0.nextVal,
   (SELECT id
    FROM GUICOMPONENT
    WHERE name = 'add.peering.partner' AND type = 'Button'),
   (SELECT id
    FROM ROLE
    WHERE name = 'auftrag.bearbeiter'), '1', '1', 0);

-- Deaktivate Button
INSERT INTO GUICOMPONENT
(ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
VALUES
  (S_GUICOMPONENT_0.nextVal, 'deactivate.peering.partner',
   'de.augustakom.hurrican.gui.auftrag.AuftragPeeringPartnerPanel',
   'Button', 'Deaktiviert den aktiven SIP-Peering-Partner eines Auftrages.', 1, 0);

INSERT INTO COMPBEHAVIOR
(ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
VALUES
  (S_COMPBEHAVIOR_0.nextVal,
   (SELECT id
    FROM GUICOMPONENT
    WHERE name = 'deactivate.peering.partner' AND type = 'Button'),
   (SELECT id
    FROM ROLE
    WHERE name = 'auftrag.bearbeiter'), '1', '1', 0);
