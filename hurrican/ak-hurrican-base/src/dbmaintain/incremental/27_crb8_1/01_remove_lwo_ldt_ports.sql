-- Additional Rangierung von Endstelle entfernen, die LWO/LDT Leisten besitzen
update t_endstelle es set ES.RANGIER_ID_ADDITIONAL=null where ES.ID in (
select distinct(e.id) from t_endstelle e
 inner join t_rangierung r on E.RANGIER_ID_ADDITIONAL=R.RANGIER_ID
 left join t_equipment eqin on R.EQ_IN_ID=EQIN.EQ_ID
 left join t_equipment eqout on R.EQ_OUT_ID=EQOUT.EQ_ID
 where (EQIN.RANG_LEISTE1 like 'LWO%' or EQIN.RANG_LEISTE1 like 'LDT%')
  and (EQOUT.RANG_LEISTE1 like 'LWO%' or EQOUT.RANG_LEISTE1 like 'LDT%')
);

-- Rangierungen entfernen, die als EQ_OUT und EQ_IN Leisten vom Typ LWO/LDT besitzen
delete from t_rangierung r where R.RANGIER_ID in (
 select rang.rangier_id from t_rangierung rang
  left join t_equipment eqin on rang.EQ_IN_ID=EQIN.EQ_ID
  left join t_equipment eqout on rang.EQ_OUT_ID=EQOUT.EQ_ID
  where (EQIN.RANG_LEISTE1 like 'LWO%' or EQIN.RANG_LEISTE1 like 'LDT%')
   and (EQOUT.RANG_LEISTE1 like 'LWO%' or EQOUT.RANG_LEISTE1 like 'LDT%')
);

-- LWO Stift von Rangierung entfernen
update t_rangierung r set r.eq_out_id=null where r.rangier_id in (
 select rang.rangier_id from t_rangierung rang
    left join t_equipment eqout on rang.EQ_OUT_ID=EQOUT.EQ_ID
  where EQOUT.RANG_LEISTE1 like 'LWO%'
);

-- Carrierbestellungs-Referenz auf LWO Ports entfernen
update t_carrierbestellung cb set CB.EQ_OUT_ID=null where CB.CB_ID in (
  select cbtmp.cb_id from t_carrierbestellung cbtmp
   inner join T_EQUIPMENT eq on CBTMP.EQ_OUT_ID=EQ.EQ_ID
   where EQ.RANG_LEISTE1 like 'LWO%'
);

delete from t_equipment eq
  where EQ.RANG_LEISTE1 like 'LWO%' or EQ.RANG_LEISTE1 like 'LDT%' and EQ.CARRIER='MNET';
