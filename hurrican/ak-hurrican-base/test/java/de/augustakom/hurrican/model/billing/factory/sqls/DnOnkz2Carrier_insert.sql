INSERT INTO DN_ONKZ_2_CARRIER (ONKZ, CARRIER)
select '${onkz}', '${carrier}'
from dual
where not EXISTS (select 1 from DN_ONKZ_2_CARRIER where ONKZ = '${onkz}' )
