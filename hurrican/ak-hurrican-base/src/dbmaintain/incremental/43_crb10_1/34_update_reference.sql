--
-- SQL-Script, um die INT_VALUE in den Referenzen für die neu eingefügten Geschäftsfällen
-- (LAE, LMAE und Protwechsel) anzupassen
--

update T_REFERENCE set INT_VALUE = 40 where ID = 8005;
update T_REFERENCE set INT_VALUE = 43 where ID = 8006;
update T_REFERENCE set INT_VALUE = 43 where ID = 8007;