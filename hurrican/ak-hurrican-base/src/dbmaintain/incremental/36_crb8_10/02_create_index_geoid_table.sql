-- Address ID und Endstelle ID indizieren
CREATE INDEX IX_GEO_ID_CACHE_STREET ON T_GEO_ID_CACHE (STREET) NOLOGGING PARALLEL 4;
CREATE INDEX IX_GEO_ID_CACHE_HOUSENUM ON T_GEO_ID_CACHE (HOUSENUM) NOLOGGING PARALLEL 4;
CREATE INDEX IX_GEO_ID_CACHE_HOUSENUMEXT ON T_GEO_ID_CACHE (HOUSENUMEXTENSION) NOLOGGING PARALLEL 4;
CREATE INDEX IX_GEO_ID_CACHE_ZIPCODE ON T_GEO_ID_CACHE (ZIPCODE) NOLOGGING PARALLEL 4;
CREATE INDEX IX_GEO_ID_CACHE_CITY ON T_GEO_ID_CACHE (CITY) NOLOGGING PARALLEL 4;