--
-- Rechte und GUI-Componenten fuer Client2Site

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'vpn.admin.c2s.tokens', 'de.augustakom.hurrican.gui.system.HurricanMainFrame',
    'MenuItem', 'MenuItem fuer die Admin Maske fuer IPSec-Client2SiteTokens', 1);
Insert into ROLE
   (ID, NAME, DESCRIPTION, APP_ID, IS_ADMIN,
    IS_PARENT)
 Values
   (s_role_0.nextval, 'ipsec.admin', 'Rolle für Admins der IPSec Aufträge.<br>Diese können z.B. die Client2Site Tokens administrieren', 1, '0',
    NULL);
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, r.id, '1', '1', g.ID
  from GUICOMPONENT g, role r
  where g.name='vpn.admin.c2s.tokens'
  and g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame'
  and r.name='ipsec.admin';
