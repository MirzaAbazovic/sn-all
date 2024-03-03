--
-- SQL-Script, das nach der Migration auf der Oracle-DB
-- ausgefuehrt werden muss.
--

alter session set nls_date_format='yyyy-mm-dd';


-- Kontrolle, ob ungueltige Positionen angelegt sind:
select a.auftrag__no, a.rec_source 
 from auftrag a 
  inner join auftragpos ap on a.auftrag__no=ap.order__no
 where ap.charge_to<ap.charge_from
  and a.oe__no in (2157,2158,2159,2160,2161,2162);


-- Kontrolle, ob in CHARGED_ITEM_INIT charged_from>charged_to  - ggf. korrigieren
select * from mlb_charged_item_init where charged_from>charged_to;


-- TODO
-- Papierrechnung bei Kunde M-net entfernen

-- in HURRICAN
--update t_faktura_nr set faktura_monat=111 where id>=8438;
