delete from USERACCOUNT where ACCOUNT_ID = (select ID from ACCOUNT where ACC_NAME = 'hurrican.web.wita.writer');
delete from USERACCOUNT where ACCOUNT_ID = (select ID from ACCOUNT where ACC_NAME = 'hurrican.wita.writer');

delete from ACCOUNT where ACC_NAME = 'hurrican.web.wita.writer';
delete from ACCOUNT where ACC_NAME = 'hurrican.wita.writer';

delete from DB where NAME='wita';