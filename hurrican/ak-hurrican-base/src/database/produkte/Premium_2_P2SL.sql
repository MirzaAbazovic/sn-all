--
-- SQL-Script fuer die Produkt-Strassenzuordnung.
-- Die Konfiguration fuer die PremiumCall Produkte wird aus der
-- Konfiguration der entsprechenden Maxi-Produkte bzw. aus den
-- 'alten' AK-Produkten geladen.
--

-- temp. Tabelle anlegen
create table p2sl_tmp (
	prod_id integer(9) not null,
	sl_id integer(9) not null,
	freigabe_id integer(9) not null);

-- Konfiguration fuer DSL2000
insert into p2sl_tmp (prod_id, sl_id, freigabe_id)
	select 330, src.sl_id, src.freigabe_id from t_prod_2_sl src where src.prod_id=324;
insert into t_prod_2_sl (prod_id, sl_id, freigabe_id) 
	select src.prod_id, src.sl_id, src.freigabe_id from p2sl_tmp src;
delete from p2sl_tmp;

-- Konfiguration fuer DSL3000
insert into p2sl_tmp (prod_id, sl_id, freigabe_id)
	select 331, src.sl_id, src.freigabe_id from t_prod_2_sl src where src.prod_id=325;
insert into t_prod_2_sl (prod_id, sl_id, freigabe_id) 
	select src.prod_id, src.sl_id, src.freigabe_id from p2sl_tmp src;
delete from p2sl_tmp;

-- Konfiguration fuer DSL6000
insert into p2sl_tmp (prod_id, sl_id, freigabe_id)
	select 332, src.sl_id, src.freigabe_id from t_prod_2_sl src where src.prod_id=326;
insert into t_prod_2_sl (prod_id, sl_id, freigabe_id) 
	select src.prod_id, src.sl_id, src.freigabe_id from p2sl_tmp src;
delete from p2sl_tmp;

-- Konfiguration fuer DSL MAX
insert into p2sl_tmp (prod_id, sl_id, freigabe_id)
	select 333, src.sl_id, src.freigabe_id from t_prod_2_sl src where src.prod_id=327;
insert into t_prod_2_sl (prod_id, sl_id, freigabe_id) 
	select src.prod_id, src.sl_id, src.freigabe_id from p2sl_tmp src;
delete from p2sl_tmp;

-- Konfiguration fuer DSL analog
insert into p2sl_tmp (prod_id, sl_id, freigabe_id)
	select 334, src.sl_id, src.freigabe_id from t_prod_2_sl src where src.prod_id=328;
insert into t_prod_2_sl (prod_id, sl_id, freigabe_id) 
	select src.prod_id, src.sl_id, src.freigabe_id from p2sl_tmp src;
delete from p2sl_tmp;

-- Konfiguration fuer DSL ISDN
insert into p2sl_tmp (prod_id, sl_id, freigabe_id)
	select 335, src.sl_id, src.freigabe_id from t_prod_2_sl src where src.prod_id=329;
insert into t_prod_2_sl (prod_id, sl_id, freigabe_id) 
	select src.prod_id, src.sl_id, src.freigabe_id from p2sl_tmp src;
delete from p2sl_tmp;

-- Konfiguration fuer ISDN MSN
insert into p2sl_tmp (prod_id, sl_id, freigabe_id)
	select 336, src.sl_id, src.freigabe_id from t_prod_2_sl src where src.prod_id=2;
insert into t_prod_2_sl (prod_id, sl_id, freigabe_id) 
	select src.prod_id, src.sl_id, src.freigabe_id from p2sl_tmp src;
delete from p2sl_tmp;

-- Konfiguration fuer ISDN TK
insert into p2sl_tmp (prod_id, sl_id, freigabe_id)
	select 337, src.sl_id, src.freigabe_id from t_prod_2_sl src where src.prod_id=1;
insert into t_prod_2_sl (prod_id, sl_id, freigabe_id) 
	select src.prod_id, src.sl_id, src.freigabe_id from p2sl_tmp src;
delete from p2sl_tmp;

-- Konfiguration fuer ISDN PMX
insert into p2sl_tmp (prod_id, sl_id, freigabe_id)
	select 338, src.sl_id, src.freigabe_id from t_prod_2_sl src where src.prod_id=3;
insert into t_prod_2_sl (prod_id, sl_id, freigabe_id) 
	select src.prod_id, src.sl_id, src.freigabe_id from p2sl_tmp src;
delete from p2sl_tmp;

-- temp. Tabelle wieder entfernen
drop table p2sl_tmp;

