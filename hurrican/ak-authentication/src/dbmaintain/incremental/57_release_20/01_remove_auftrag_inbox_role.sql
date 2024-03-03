delete from userrole where role_id in (
  select r.id from role r where r.name in ('auftrag.inbox.bearbeiter', 'auftrag.inbox.viewer'));

delete from compbehavior where role_id in (
  select r.id from role r where r.name in ('auftrag.inbox.bearbeiter', 'auftrag.inbox.viewer'));

delete from roletree
  where
    role_id in (select r.id from role r where r.name in ('auftrag.inbox.bearbeiter', 'auftrag.inbox.viewer'))
    or
    parent_role_id in (select r.id from role r where r.name in ('auftrag.inbox.bearbeiter', 'auftrag.inbox.viewer'));

delete from role where name in ('auftrag.inbox.bearbeiter', 'auftrag.inbox.viewer');

