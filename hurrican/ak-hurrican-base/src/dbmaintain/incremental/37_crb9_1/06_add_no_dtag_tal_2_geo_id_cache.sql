ALTER TABLE T_GEO_ID_CACHE ADD NO_DTAG_TAL CHAR(1);
COMMENT ON COLUMN T_GEO_ID_CACHE.NO_DTAG_TAL IS 'MNet versorgte Adresse, die keinem DTAG HVT zugeordnet werden darf.';
