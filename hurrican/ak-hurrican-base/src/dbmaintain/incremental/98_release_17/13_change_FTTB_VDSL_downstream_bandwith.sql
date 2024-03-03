-- modify downstream bandwidth of FTTB_VDSL to 150 Mbit, so that the bandwidth will fit to the DPO-Baugruppe
UPDATE T_PHYSIKTYP
SET MAX_BANDWIDTH_DOWNSTREAM = 150000, CHANGED_AT = sysdate, VERSION = 1
WHERE id = 800 AND name = 'FTTB_VDSL2';