--
-- Konfiguration von Prod2Prod fuer die PremiumCall-Produkte.
-- Konfiguration wird von den maxi-Produkten uebernommen
--

-- temp. Tabelle anlegen
create table p2p_temp (
	prod_src integer(9) not null, 
	prod_dest integer(9) not null, 
	physikaend_typ integer(9) not null, 
	chain_id integer(9) not null);

-- neue Produkte als Ziel-Produkt
-- DSL2000 
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select src.prod_src, 330, src.physikaend_typ, src.chain_id from t_prod_2_prod src 
	where src.prod_dest=324 and src.physikaend_typ in (5000,5005);
insert into t_prod_2_prod (prod_src, prod_dest, physikaend_typ, chain_id)
	select tmp.prod_src, tmp.prod_dest, tmp.physikaend_typ, tmp.chain_id from p2p_temp tmp;
delete from p2p_temp;

-- DSL3000 
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select src.prod_src, 331, src.physikaend_typ, src.chain_id from t_prod_2_prod src 
	where src.prod_dest=325 and src.physikaend_typ in (5000,5005);
insert into t_prod_2_prod (prod_src, prod_dest, physikaend_typ, chain_id)
	select tmp.prod_src, tmp.prod_dest, tmp.physikaend_typ, tmp.chain_id from p2p_temp tmp;
delete from p2p_temp;

-- DSL6000 
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select src.prod_src, 332, src.physikaend_typ, src.chain_id from t_prod_2_prod src 
	where src.prod_dest=326 and src.physikaend_typ in (5000,5005);
insert into t_prod_2_prod (prod_src, prod_dest, physikaend_typ, chain_id)
	select tmp.prod_src, tmp.prod_dest, tmp.physikaend_typ, tmp.chain_id from p2p_temp tmp;
delete from p2p_temp;

-- DSL MAX
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select src.prod_src, 333, src.physikaend_typ, src.chain_id from t_prod_2_prod src 
	where src.prod_dest=327 and src.physikaend_typ in (5000,5005);
insert into t_prod_2_prod (prod_src, prod_dest, physikaend_typ, chain_id)
	select tmp.prod_src, tmp.prod_dest, tmp.physikaend_typ, tmp.chain_id from p2p_temp tmp;
delete from p2p_temp;

-- DSL analog
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select src.prod_src, 334, src.physikaend_typ, src.chain_id from t_prod_2_prod src 
	where src.prod_dest=328 and src.physikaend_typ in (5000,5005);
insert into t_prod_2_prod (prod_src, prod_dest, physikaend_typ, chain_id)
	select tmp.prod_src, tmp.prod_dest, tmp.physikaend_typ, tmp.chain_id from p2p_temp tmp;
delete from p2p_temp;

-- DSL ISDN
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select src.prod_src, 335, src.physikaend_typ, src.chain_id from t_prod_2_prod src 
	where src.prod_dest=329 and src.physikaend_typ in (5000,5005);
insert into t_prod_2_prod (prod_src, prod_dest, physikaend_typ, chain_id)
	select tmp.prod_src, tmp.prod_dest, tmp.physikaend_typ, tmp.chain_id from p2p_temp tmp;
delete from p2p_temp;

-- ISDN MSN
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select src.prod_src, 336, src.physikaend_typ, src.chain_id from t_prod_2_prod src where src.prod_dest=2;
insert into t_prod_2_prod (prod_src, prod_dest, physikaend_typ, chain_id)
	select tmp.prod_src, tmp.prod_dest, tmp.physikaend_typ, tmp.chain_id from p2p_temp tmp;
delete from p2p_temp;

-- ISDN TK
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select src.prod_src, 337, src.physikaend_typ, src.chain_id from t_prod_2_prod src where src.prod_dest=1;
insert into t_prod_2_prod (prod_src, prod_dest, physikaend_typ, chain_id)
	select tmp.prod_src, tmp.prod_dest, tmp.physikaend_typ, tmp.chain_id from p2p_temp tmp;
delete from p2p_temp;

-- ISDN PMX
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select src.prod_src, 338, src.physikaend_typ, src.chain_id from t_prod_2_prod src where src.prod_dest=3;
insert into t_prod_2_prod (prod_src, prod_dest, physikaend_typ, chain_id)
	select tmp.prod_src, tmp.prod_dest, tmp.physikaend_typ, tmp.chain_id from p2p_temp tmp;
delete from p2p_temp;


-- neue Produkte als Src-Produkt
-- DSL2000
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select 330, src.prod_dest, src.physikaend_typ, src.chain_id from t_prod_2_prod src 
	where src.prod_src=324 and physikaend_typ in (5000,5005);
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select 330, src.prod_dest+6, src.physikaend_typ, src.chain_id from t_prod_2_prod src 
	where src.prod_src=324 and physikaend_typ in (5004);
insert into t_prod_2_prod (prod_src, prod_dest, physikaend_typ, chain_id)
	select tmp.prod_src, tmp.prod_dest, tmp.physikaend_typ, tmp.chain_id from p2p_temp tmp;
delete from p2p_temp;

-- DSL3000
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select 331, src.prod_dest, src.physikaend_typ, src.chain_id from t_prod_2_prod src 
	where src.prod_src=325 and physikaend_typ in (5000,5005);
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select 331, src.prod_dest+6, src.physikaend_typ, src.chain_id from t_prod_2_prod src 
	where src.prod_src=325 and physikaend_typ in (5004);
insert into t_prod_2_prod (prod_src, prod_dest, physikaend_typ, chain_id)
	select tmp.prod_src, tmp.prod_dest, tmp.physikaend_typ, tmp.chain_id from p2p_temp tmp;
delete from p2p_temp;

-- DSL6000
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select 332, src.prod_dest, src.physikaend_typ, src.chain_id from t_prod_2_prod src 
	where src.prod_src=326 and physikaend_typ in (5000,5005);
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select 332, src.prod_dest+6, src.physikaend_typ, src.chain_id from t_prod_2_prod src 
	where src.prod_src=326 and physikaend_typ in (5004);
insert into t_prod_2_prod (prod_src, prod_dest, physikaend_typ, chain_id)
	select tmp.prod_src, tmp.prod_dest, tmp.physikaend_typ, tmp.chain_id from p2p_temp tmp;
delete from p2p_temp;

-- DSL MAX
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select 333, src.prod_dest, src.physikaend_typ, src.chain_id from t_prod_2_prod src 
	where src.prod_src=327 and physikaend_typ in (5000,5005);
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select 333, src.prod_dest+6, src.physikaend_typ, src.chain_id from t_prod_2_prod src 
	where src.prod_src=327 and physikaend_typ in (5004);
insert into t_prod_2_prod (prod_src, prod_dest, physikaend_typ, chain_id)
	select tmp.prod_src, tmp.prod_dest, tmp.physikaend_typ, tmp.chain_id from p2p_temp tmp;
delete from p2p_temp;

-- DSL analog
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select 334, src.prod_dest, src.physikaend_typ, src.chain_id from t_prod_2_prod src where src.prod_src=328;
insert into t_prod_2_prod (prod_src, prod_dest, physikaend_typ, chain_id)
	select tmp.prod_src, tmp.prod_dest, tmp.physikaend_typ, tmp.chain_id from p2p_temp tmp;
delete from p2p_temp;	
	
-- DSL ISDN
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select 335, src.prod_dest, src.physikaend_typ, src.chain_id from t_prod_2_prod src where src.prod_src=329;
insert into t_prod_2_prod (prod_src, prod_dest, physikaend_typ, chain_id)
	select tmp.prod_src, tmp.prod_dest, tmp.physikaend_typ, tmp.chain_id from p2p_temp tmp;
delete from p2p_temp;	
	
-- ISDN MSN
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select 336, src.prod_dest, src.physikaend_typ, src.chain_id from t_prod_2_prod src where src.prod_src=2;
insert into t_prod_2_prod (prod_src, prod_dest, physikaend_typ, chain_id)
	select tmp.prod_src, tmp.prod_dest, tmp.physikaend_typ, tmp.chain_id from p2p_temp tmp;
delete from p2p_temp;		
	
-- ISDN TK
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select 337, src.prod_dest, src.physikaend_typ, src.chain_id from t_prod_2_prod src where src.prod_src=1;
insert into t_prod_2_prod (prod_src, prod_dest, physikaend_typ, chain_id)
	select tmp.prod_src, tmp.prod_dest, tmp.physikaend_typ, tmp.chain_id from p2p_temp tmp;
delete from p2p_temp;			
		
-- ISDN PMX
insert into p2p_temp (prod_src, prod_dest, physikaend_typ, chain_id)
	select 338, src.prod_dest, src.physikaend_typ, src.chain_id from t_prod_2_prod src where src.prod_src=3;
insert into t_prod_2_prod (prod_src, prod_dest, physikaend_typ, chain_id)
	select tmp.prod_src, tmp.prod_dest, tmp.physikaend_typ, tmp.chain_id from p2p_temp tmp;
delete from p2p_temp;	
	
	
drop table p2p_temp;


