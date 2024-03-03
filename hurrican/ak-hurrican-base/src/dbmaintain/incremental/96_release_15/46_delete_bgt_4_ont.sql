-- Die Alcatel-Lucent ONT I-010G-P hat keine POTS und RF Ports! Diese Baugruppentypen wieder dropen.

-- I-010G-P_RF
--  BG_TYP_2_PHYSIK_TYP bereinigen
DELETE FROM T_HW_BG_TYP_2_PHYSIK_TYP WHERE BAUGRUPPEN_TYP_ID = (SELECT ID from T_HW_BAUGRUPPEN_TYP WHERE NAME = 'I-010G-P_RF' AND HW_TYPE_NAME = 'ONT');
--  RF Baugruppe loeschen
DELETE FROM T_HW_BAUGRUPPEN_TYP WHERE NAME = 'I-010G-P_RF' AND HW_TYPE_NAME = 'ONT';

-- I-010G-P_POTS
-- BG_TYP_2_PHYSIK_TYP bereinigen
DELETE FROM T_HW_BG_TYP_2_PHYSIK_TYP WHERE BAUGRUPPEN_TYP_ID = (SELECT ID from T_HW_BAUGRUPPEN_TYP WHERE NAME = 'I-010G-P_POTS' AND HW_TYPE_NAME = 'ONT');
--  POTS loeschen
DELETE FROM T_HW_BAUGRUPPEN_TYP WHERE NAME = 'I-010G-P_POTS' AND HW_TYPE_NAME = 'ONT';