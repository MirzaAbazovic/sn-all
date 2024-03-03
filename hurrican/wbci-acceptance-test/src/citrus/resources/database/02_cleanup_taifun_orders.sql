-- Alle Rufnummern mit ONKZ=08888 umschreiben; sind durch TaifunDataFactory angelegt worden
update DN set ONKZ='00000' where ONKZ = '08888';

-- Adressen mit GeoId <= 999999 umschreiben; ebenfalls durch TaifunDataFactory angelegt
update ADDRESS set VENTO_GEO_ID=null where VENTO_GEO_ID<=999999;
