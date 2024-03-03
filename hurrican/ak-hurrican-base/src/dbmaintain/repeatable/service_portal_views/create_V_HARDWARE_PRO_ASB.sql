-- Abfrage des ASB pro DSLAM / EWSD / MDU
-- (wird aus dem Service-Portal aufgerufen)

CREATE OR REPLACE VIEW V_HARDWARE_PRO_ASB as
	SELECT
		r1.*,
		sw.NAME AS SWITCH,
		d1.DLU_NUMBER,
		b.ID       BG_ID,
		b.MOD_NUMBER,
		type.NAME  BG_NAME
	from T_HW_RACK r1
		left join T_HW_RACK_DLU d1 on (r1.ID = d1.RACK_ID)
		left join T_HW_SWITCH sw on (sw.ID = d1.SWITCH)
		left join T_HW_BAUGRUPPE b on (r1.ID = b.RACK_ID)
		left join T_HW_BAUGRUPPEN_TYP type on (b.HW_BG_TYP_ID = type.ID)
	where r1.RACK_TYP in ('DLU','DSLAM','MDU');


GRANT SELECT ON V_HARDWARE_PRO_ASB TO HURRICAN_TECH_MUC;