-- ANF-217.01 neue Physiktyp für DPO VDSL Port erstellen
-- Bandbreite des PT FTTB_VDSL2 (alte MDUs only) auf 100MBit/s beschraenken
UPDATE T_PHYSIKTYP
SET MAX_BANDWIDTH_DOWNSTREAM = 100000
WHERE NAME = 'FTTB_VDSL2';

-- Zuordnung der DPO Baugruppe zum PT von MA5651_VDSL2 -> FTTB_VDSL2 nach MA5651_VDSL2 -> FTTB_DPO_VDSL2 anpassen
UPDATE T_HW_BG_TYP_2_PHYSIK_TYP
SET PHYSIKTYP_ID = (SELECT ID
                    FROM T_PHYSIKTYP
                    WHERE NAME = 'FTTB_DPO_VDSL2')
WHERE BAUGRUPPEN_TYP_ID = (SELECT ID
                           FROM T_HW_BAUGRUPPEN_TYP
                           WHERE NAME = 'MA5651_VDSL2' AND HW_TYPE_NAME = 'DPO')
      AND PHYSIKTYP_ID = (SELECT ID
                          FROM T_PHYSIKTYP
                          WHERE NAME = 'FTTB_VDSL2');
