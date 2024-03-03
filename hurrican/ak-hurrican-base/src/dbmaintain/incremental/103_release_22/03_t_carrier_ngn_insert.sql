insert into T_CARRIER (ID, TEXT, ORDER_NO, CB_NOTWENDIG, EL_TAL_EMPF_ID, COMPANY_NAME, PORTIERUNGSKENNUNG, VA_MODUS, ITU_CARRIER_CODE, HAS_WITA, CUDA_KUENDIGUNG)
select S_T_CARRIER_0.nextval, 'MNET NGN', 3, 1,'D235-089', 'M-net Telekommunikations GmbH', 'D235', 'WBCI', 'DEU.MNET', 1, 0
from dual where not exists (select 1 from T_CARRIER tt where tt.text = 'MNET NGN');

update T_CARRIER t set t.PORTIERUNGSKENNUNG = 'D052'
where t.id = 17 and t.text='MNET';
