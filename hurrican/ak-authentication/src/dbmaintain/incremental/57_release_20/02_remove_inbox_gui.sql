delete from COMPBEHAVIOR where COMP_ID in
  (select g.ID from GUICOMPONENT g where g.parent='de.augustakom.hurrican.gui.tools.scv.AuftragInboxPanel');
delete from GUICOMPONENT g where g.parent='de.augustakom.hurrican.gui.tools.scv.AuftragInboxPanel';

delete from COMPBEHAVIOR where COMP_ID in
  (select g.ID from GUICOMPONENT g where g.name='auftrag.inbox.action');
delete from GUICOMPONENT g where g.name='auftrag.inbox.action';
