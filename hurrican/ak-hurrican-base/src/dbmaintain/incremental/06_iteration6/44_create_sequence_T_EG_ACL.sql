--
-- Let the sequence start at 1000, which should be high enough to accomodate all values from KuP
--

drop SEQUENCE S_T_EG_ACL_0;
create SEQUENCE S_T_EG_ACL_0 start with 1000;
grant select on S_T_EG_ACL_0 to public;
