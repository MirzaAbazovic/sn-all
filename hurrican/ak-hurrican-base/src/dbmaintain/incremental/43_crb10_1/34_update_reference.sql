--
-- SQL-Script, um die INT_VALUE in den Referenzen f�r die neu eingef�gten Gesch�ftsf�llen
-- (LAE, LMAE und Protwechsel) anzupassen
--

update T_REFERENCE set INT_VALUE = 40 where ID = 8005;
update T_REFERENCE set INT_VALUE = 43 where ID = 8006;
update T_REFERENCE set INT_VALUE = 43 where ID = 8007;