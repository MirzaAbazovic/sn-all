--
-- View to get all IP addresses from T_IP_ADDRESS - excluded 0.0.0.0/0 as those addresses need not be considered
--
create or replace force VIEW MIGRATION_IPBINARY as
  select 
    ip.ID as ID, 
    ip.ADDRESS as IP_ADDRESS,
    ip.BINARY_REPRESENTATION as BINARY_REPRESENTATION 
  from 
    T_IP_ADDRESS ip
  where   
    ip.ADDRESS != '0.0.0.0/0'
  order by
    ip.ID ASC
;
 
grant select on MIGRATION_IPBINARY to R_HURRICAN_USER;
