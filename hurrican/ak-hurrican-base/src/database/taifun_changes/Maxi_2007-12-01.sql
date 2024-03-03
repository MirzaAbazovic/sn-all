--
-- Maxi-Preisaenderungen zum 01.12.2007
--
  
insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (202, 'Maxi-Aenderung 2007-12-01', '2007-11-30', '2007-12-01',
    38932, '16000', 20426, '18000', 2.43, 2.43, '2,43', 0);


-- Update der Leistungskonfiguration (Mapping auf Hurrican)
alter session set nls_date_format='yyyy-mm-dd';
-- Maxi
update LEISTUNG set EXT_LEISTUNG__NO=10023 where LEISTUNG_NO=31300;
-- MaxiKomplett
update LEISTUNG set EXT_LEISTUNG__NO=10023 where LEISTUNG_NO=38931;
-- Maxi Pur
update LEISTUNG set EXT_LEISTUNG__NO=10023 where LEISTUNG_NO=38936;
update LEISTUNG set EXT_LEISTUNG__NO=10023 where LEISTUNG_NO=38946; 



