delete from T_CARRIER tt where tt.text = 'MNET NGN';

insert into T_CARRIER (ID, TEXT, ORDER_NO, CB_NOTWENDIG, EL_TAL_EMPF_ID, COMPANY_NAME, PORTIERUNGSKENNUNG, VA_MODUS, ITU_CARRIER_CODE, HAS_WITA, CUDA_KUENDIGUNG)
select 535, 'MNET NGN', 3, 1,'D235-089', 'M-net Telekommunikations GmbH', 'D235', 'WBCI', 'DEU.MNET', 1, 0
from dual where not exists (select 1 from T_CARRIER tt where tt.id = 535);
