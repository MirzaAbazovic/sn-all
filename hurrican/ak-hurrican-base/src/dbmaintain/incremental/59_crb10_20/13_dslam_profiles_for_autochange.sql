alter table T_DSLAM_PROFILE add ENABLED_FOR_AUTOCHANGE CHAR(1);

update T_DSLAM_PROFILE set ENABLED_FOR_AUTOCHANGE='0' where
  (lower(NAME) like '%adsl1force%'
   or lower(NAME) like '%l2enabled%'
   or lower(NAME) like '%nol2%'
   or lower(NAME) like '%9db%'
   or lower(NAME) like '%12db%'
   or NAME like 'MD%'    -- FTTX Profile auch ausschliessen!
   or NAME like 'KiTa%'  -- Sonderprofil fuer Kindertagesstaetten auch ausschliessen!
  );
  
update T_DSLAM_PROFILE set ENABLED_FOR_AUTOCHANGE='1' where ENABLED_FOR_AUTOCHANGE is null;

alter table T_DSLAM_PROFILE modify ENABLED_FOR_AUTOCHANGE NOT NULL;
