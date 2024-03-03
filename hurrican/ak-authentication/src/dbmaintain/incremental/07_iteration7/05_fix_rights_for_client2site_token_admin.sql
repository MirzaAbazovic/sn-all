--
-- Rechte und GUI-Componenten fuer Client2Site fixen. Falscher Name.

update GUICOMPONENT g set NAME = 'open.vpn.admin.c2s.tokens.frame.action'
where g.name = 'vpn.admin.c2s.tokens'
  and g.parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame';
