-- bg count and port count were switched

UPDATE T_HW_SUBRACK_TYP SET BG_COUNT = 16, PORT_COUNT = 48 WHERE NAME = 'NFXS-A';
UPDATE T_HW_SUBRACK_TYP SET BG_COUNT = 8, PORT_COUNT = 48 WHERE NAME = 'NFXS-B';
UPDATE T_HW_SUBRACK_TYP SET BG_COUNT = 16, PORT_COUNT = 12 WHERE NAME = 'ALTS-F';
UPDATE T_HW_SUBRACK_TYP SET BG_COUNT = 16, PORT_COUNT = 24 WHERE NAME = 'ALTS-M';
UPDATE T_HW_SUBRACK_TYP SET BG_COUNT = 5, PORT_COUNT = 24 WHERE NAME = 'UMRAM-A';
UPDATE T_HW_SUBRACK_TYP SET BG_COUNT = 16, PORT_COUNT = 48 WHERE NAME = 'ALTS-T';
