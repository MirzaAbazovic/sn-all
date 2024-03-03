alter table t_equipment drop column HW_PORT;

alter table t_rangierungs_material rename column HW_PORT to HW_BG_TYP_NAME;
update t_rangierungs_material set HW_BG_TYP_NAME='SUADSL:32I' where HW_BG_TYP_NAME='SUADSL';
