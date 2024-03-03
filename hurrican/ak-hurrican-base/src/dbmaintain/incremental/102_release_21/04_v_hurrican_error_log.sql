CREATE OR REPLACE VIEW V_HURRICAN_ERROR_LOG AS
select 
  elog.ID, elog.ERROR_NAME, 
  elog.ERROR_DESCRIPTION, elog.CREATED_AT, 
  elog.GERAETEBEZ, 
  elog.SERIAL_NO as NEU_SERIAL_NO,
  CASE WHEN r.id is not null THEN 'Ja' ELSE 'Nein' END as hw_record_found,
  CASE WHEN ont.rack_id is not null THEN ont.serial_no ELSE dpo.serial_no END as hw_record_serial_no,
  r.id as rack_id,
  r.rack_typ as rack_typ,
  r.hvt_id_standort as rack_hvt_id_standort,
  r.hvt_raum_id as rack_hvt_raum_id
from T_ERROR_LOG elog
left outer join T_HW_RACK r on r.GERAETEBEZ = elog.GERAETEBEZ
left outer join T_HW_RACK_ONT ont on ont.RACK_ID = r.ID
left outer join T_HW_RACK_DPO dpo on dpo.RACK_ID = r.ID;
