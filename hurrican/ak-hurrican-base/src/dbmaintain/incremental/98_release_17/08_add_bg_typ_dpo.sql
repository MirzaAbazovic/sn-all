-- ANF-217.01 HW_BAUGRUPPEN_TYP
INSERT INTO T_HW_BAUGRUPPEN_TYP (ID, NAME, PORT_COUNT, DESCRIPTION, IS_ACTIVE, HW_SCHNITTSTELLE_NAME, HW_TYPE_NAME,
                                 HVT_TECHNIK_ID, VERSION, TUNNELING, DEF_SCHICHT2_PROTOKOLL, MAX_BANDWIDTH_DOWNSTREAM,
                                 MAX_BANDWIDTH_UPSTREAM)
VALUES
  (S_T_HW_BAUGRUPPEN_TYP_0.nextVal, 'MA5651_VDSL2', 1, 'VDSL-Port der Huawei DPO', '1', 'VDSL2', 'DPO',
   2, 0, NULL, NULL, 150000,
   50000);

-- FTTB_VDSL2 Physiktyp mit Baugruppe mappen
INSERT INTO T_HW_BG_TYP_2_PHYSIK_TYP
(ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID, VERSION)
VALUES
  (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval, (SELECT
                                            bgt.id
                                          FROM t_hw_baugruppen_typ bgt
                                          WHERE bgt.name = 'MA5651_VDSL2' AND bgt.hw_type_name = 'DPO'), 800, 0);
