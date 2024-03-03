--
-- Setze TDN-Kind of use Produkt auf C fuer Mnet vor Ort
--

update t_produkt
set TDN_KIND_OF_USE_PRODUCT = 'C'
where PROD_ID in (350,351,352);