ALTER TABLE T_GEO_ID_2_TECH_LOCATION ADD TAL_LENGTH_TRUSTED CHAR(1) DEFAULT 0;
ALTER TABLE T_GEO_ID_2_TECH_LOCATION MODIFY TAL_LENGTH_TRUSTED CHAR(1) NOT NULL;
COMMENT ON COLUMN T_GEO_ID_2_TECH_LOCATION.TAL_LENGTH_TRUSTED IS 'TAL Laengen aus Carrierbestellungen sind gesichert.';
