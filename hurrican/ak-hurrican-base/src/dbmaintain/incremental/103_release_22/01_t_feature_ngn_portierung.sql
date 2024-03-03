insert into T_FEATURE (ID, NAME, FLAG, VERSION)
  select S_T_FEATURE_0.nextVal, 'NGN_PORTIERING_WEB_SERVICE', '1', 0
  from dual where not exists (select 1 from T_FEATURE tt where tt.NAME = 'NGN_PORTIERING_WEB_SERVICE');
