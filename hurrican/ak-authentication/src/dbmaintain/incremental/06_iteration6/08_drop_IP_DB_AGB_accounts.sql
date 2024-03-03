-- Loescht die Accounts für die IP-DB in Augsburg

delete from useraccount where db_id = 5;
delete from account where db_id = 5;
delete from db where id = 5;
commit;