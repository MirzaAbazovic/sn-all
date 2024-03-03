
UPDATE GUICOMPONENT cmp SET TYPE = 'Button'
WHERE cmp.name ='move.to.rl' and cmp.parent in ('de.augustakom.hurrican.gui.verlauf.BauauftragDISPOPanel',
'de.augustakom.hurrican.gui.verlauf.BauauftragDISPOPanel.NP',
'de.augustakom.hurrican.gui.verlauf.ProjektierungDispoPanel',
'de.augustakom.hurrican.gui.verlauf.ProjektierungDispoPanel.NP');

insert into COMPBEHAVIOR bh (id, comp_id, role_id, visible, executable, version)
select S_COMPBEHAVIOR_0.nextval, gc.id, r.id, 1, 1, 0
from dual,
  (select id from GUICOMPONENT where name ='move.to.rl' and parent ='de.augustakom.hurrican.gui.verlauf.BauauftragDISPOPanel' ) gc,
  (select id from role where name in ('verlauf.view')) r
where not exists (
  select 'x' from COMPBEHAVIOR cc
  where cc.comp_id = gc.id
  and   cc.role_id = r.id
);

