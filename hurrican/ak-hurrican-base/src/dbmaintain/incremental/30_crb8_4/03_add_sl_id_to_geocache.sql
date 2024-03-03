alter table T_GEO_ID_CACHE add OLD_STREET_ID NUMBER(10);
comment on column T_GEO_ID_CACHE.OLD_STREET_ID is 'ID aus T_STRASSENLISTE; notwendig fuer Geo-ID Migration; kann danach entfernt werden!';