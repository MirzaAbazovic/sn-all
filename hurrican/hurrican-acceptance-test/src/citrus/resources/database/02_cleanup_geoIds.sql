-- Delete all endstellen and geoId data created during TvProvider tests
delete from T_ES_LTG_DATEN where es_id in (
  select es1.id from T_ENDSTELLE es1 join T_GEO_ID_CACHE geoId on (geoId.ID = es1.GEO_ID AND geoId.ID > 999999999)
);
delete from T_ENDSTELLE_CONNECT where ENDSTELLE_ID in (
  select es1.id from T_ENDSTELLE es1 join T_GEO_ID_CACHE geoId on (geoId.ID = es1.GEO_ID AND geoId.ID > 999999999)
);
delete from T_ENDSTELLE es where rowid in (
  select es1.rowid from T_ENDSTELLE es1 join T_GEO_ID_CACHE geoId on (geoId.ID = es1.GEO_ID AND geoId.ID > 999999999)
);

delete from T_GEO_ID_2_TECH_LOCATION where GEO_ID > 999999999;
delete from T_GEO_ID_CACHE where ID > 999999999;
delete from T_GEO_ID_STREET_SECTION where ID > 999999999;
delete from T_GEO_ID_ZIPCODE zc where zc.id > 9999999 and not exists (select * from T_GEO_ID_STREET_SECTION s where s.ZIP_CODE_ID = zc.id);
delete from T_GEO_ID_CITY c where c.id > 9999999 and not exists (select zc.id from T_GEO_ID_ZIPCODE zc where zc.CITY_ID = c.id);
delete from  T_GEO_ID_COUNTRY country where id > 9999999 and not exists (select city.id from T_GEO_ID_CITY city where city.COUNTRY_ID = country.id);
