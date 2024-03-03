-- Korrektur Hersteller des Alcatel Baugruppentyps
UPDATE T_HW_BAUGRUPPEN_TYP
SET HVT_TECHNIK_ID = 6
WHERE NAME = 'G-010V-P_VDSL2' AND HW_TYPE_NAME = 'DPO';
