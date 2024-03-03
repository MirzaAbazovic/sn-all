--
-- View um fehlerhafte Subnet-Migrationen zu ermitteln
--
create or replace force view MIGRATION_RESULT_IPSUBNET as
select 
    log.CLASSIFICATION_STRING,
    log.MESSAGE
  from 
    HURRICAN_MIG_RESULT res
    left join HURRICAN_MIG_LOG log on res.ID=log.MIGRESULT_ID
  where
    res.MIGRATION_NAME='IpSubnetMigration'
    and log.CLASSIFICATION=1;

grant select on MIGRATION_RESULT_IPSUBNET to R_HURRICAN_USER;
