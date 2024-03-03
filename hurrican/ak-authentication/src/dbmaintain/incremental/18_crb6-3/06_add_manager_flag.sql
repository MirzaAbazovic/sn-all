
alter table users add MANAGER CHAR(1);

update USERS set MANAGER='1' where LOGINNAME='baumannka' or LOGINNAME='Erl' or LOGINNAME='LuebkeEc';

