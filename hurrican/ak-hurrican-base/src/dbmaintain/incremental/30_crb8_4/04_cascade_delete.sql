alter table t_geo_id_2_tech_location drop constraint FK_GEOIDMAPPING_2_GEO;
ALTER TABLE T_GEO_ID_2_TECH_LOCATION ADD CONSTRAINT FK_GEOIDMAPPING_2_GEO
  FOREIGN KEY (GEO_ID) REFERENCES T_GEO_ID_CACHE (ID)
  on delete cascade;