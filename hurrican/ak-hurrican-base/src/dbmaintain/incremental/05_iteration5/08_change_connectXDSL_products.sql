--
-- Aendert die Account-Einstellungen der Produkte 460 u. 461
-- (Connect SDSL und Connect ADSL)
-- zukuenftig kein Account mehr notwendig!
--

update t_produkt set li_nr=null, account_vors=null where prod_id in (460, 461);

