delete from COMPBEHAVIOR where id in (
select bh.id
from COMPBEHAVIOR bh,
  (select id from GUICOMPONENT where name ='move.to.rl' and parent ='de.augustakom.hurrican.gui.verlauf.BauauftragDISPOPanel' ) gc,
  (select id from role where name in ('verlauf.view')) r
where gc.id = bh.COMP_ID
  and r.id = bh.ROLE_ID
);

insert into COMPBEHAVIOR bh (id, comp_id, role_id, visible, executable, version)
select S_COMPBEHAVIOR_0.nextval, gc.id, r.id, 1, 1, 0
from dual,
  (select id from GUICOMPONENT where name ='move.to.rl' and parent ='de.augustakom.hurrican.gui.verlauf.BauauftragDISPOPanel' ) gc,
  (select id from role where name in ('verlauf.dispo')) r
where not exists (
  select 'x' from COMPBEHAVIOR cc
  where cc.comp_id = gc.id
  and   cc.role_id = r.id
);
