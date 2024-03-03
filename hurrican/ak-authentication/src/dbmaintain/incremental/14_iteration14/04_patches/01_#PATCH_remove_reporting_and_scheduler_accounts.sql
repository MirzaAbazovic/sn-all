delete from users
where id in (
select u.id from
account ac join
application ap
on AC.APP_ID = AP.ID
join USERACCOUNT ua
on ac.id = UA.ACCOUNT_ID
join users u
on UA.USER_ID = u.id
where AP.ID in (2,3))
/


delete from account
where id in (
select ac.id from
account ac join
application ap
on AC.APP_ID = AP.ID
where AP.ID in (2,3))
/

delete from userrole
where id in (
select ur.id
from role rl
join application ac
on RL.APP_ID = AC.ID
join USERROLE ur
on RL.ID = UR.ROLE_ID
where
ac.id in (2,3))
/

delete from role
where id in (
select rl.id
from role rl
join application ac
on RL.APP_ID = AC.ID
where
ac.id in (2,3))
/

delete from application
where id in (2,3)
/
