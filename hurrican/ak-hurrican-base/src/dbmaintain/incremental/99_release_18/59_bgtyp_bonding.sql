alter table T_HW_BAUGRUPPEN_TYP add BONDING_CAPABLE CHAR(1);
update T_HW_BAUGRUPPEN_TYP set BONDING_CAPABLE='0';
update T_HW_BAUGRUPPEN_TYP set BONDING_CAPABLE='1'
  where NAME in ('NVLTQ', 'NVLTH', 'NALTD');
alter table T_HW_BAUGRUPPEN_TYP modify BONDING_CAPABLE not null;