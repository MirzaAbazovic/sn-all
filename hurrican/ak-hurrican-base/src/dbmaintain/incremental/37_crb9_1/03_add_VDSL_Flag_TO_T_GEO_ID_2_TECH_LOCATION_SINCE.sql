ALTER TABLE T_GEO_ID_2_TECH_LOCATION DROP COLUMN VDSL_AN_HVT_AVAILABLE;
ALTER TABLE T_GEO_ID_2_TECH_LOCATION ADD VDSL_AN_HVT_AVAILABLE_SINCE DATE DEFAULT NULL;
comment on column T_GEO_ID_2_TECH_LOCATION.VDSL_AN_HVT_AVAILABLE_SINCE is 'Gibt an seit wann VDSL an einem HVT verfuegbar ist';