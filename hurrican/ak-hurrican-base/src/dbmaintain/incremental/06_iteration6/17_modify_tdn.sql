--
-- modfiy TDN column to 'not null'
--

alter table t_tdn modify tdn varchar2(50) not null;
