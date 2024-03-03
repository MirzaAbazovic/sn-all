--
-- Let the sequence start at 315, with is the last value from KuP
--

drop SEQUENCE S_T_EG_ACL_0;
create SEQUENCE S_T_EG_ACL_0 start with 315;
grant select on S_T_EG_ACL_0 to public;
