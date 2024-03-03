-- for all voip products without switch, set the switch to MUC06
update T_PRODUKT p set p.SWITCH = (select ID from T_HW_SWITCH where NAME = 'MUC06')
where p.prod_id in (
  select distinct p.prod_id from t_produkt p
  inner join T_PROD_2_TECH_LEISTUNG p2tl on p2tl.prod_id = p.prod_id
  inner join T_TECH_LEISTUNG tl on tl.id = p2tl.tech_ls_id
  where tl.typ in ('VOIP', 'VOIP_ADD', 'VOIP_PROFILE') and p.switch is null
);
