--
-- SQL-Script, um Trigger auf der AUTHENTICATION-DB anzulegen.
--

DROP TRIGGER TRD_DELETE_USER;

create trigger TRD_DELETE_USER
  before delete on users for each row
begin
    delete from usersession where user_id = :old.id;
    delete from userrole where user_id = :old.id;
    delete from useraccount where user_id = :old.id;
end;
/
