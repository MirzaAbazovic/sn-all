
delete from t_reference where TYPE='ANSCHLUSSDOSE';

update t_equipment set HW_SCHNITTSTELLE=null
  where HW_SCHNITTSTELLE in ('TAE', 'UAE', 'VDO', 'OAD SC Kupplung');
