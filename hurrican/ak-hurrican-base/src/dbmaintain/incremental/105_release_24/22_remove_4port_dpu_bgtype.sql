-- ANF-767: MA5811S_AE04 ist keine tatsaechlich existierende Baugruppe
-- Mapping auf Physiktyp loeschen
DELETE FROM T_HW_BG_TYP_2_PHYSIK_TYP
WHERE BAUGRUPPEN_TYP_ID = (SELECT bgt.id FROM T_HW_BAUGRUPPEN_TYP bgt WHERE bgt.name = 'MA5811S_AE04');

-- Baugruppentyp loeschen (Annahme: Es existieren noch keine DPU Baugruppen)
DELETE FROM T_HW_BAUGRUPPEN_TYP WHERE NAME = 'MA5811S_AE04';
