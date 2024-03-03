-- pbit von number(19) auf number (10) ändern
update T_PRODUKT t set t.P_BIT_DATEN = null , t.P_BIT_VOIP = null
where t.prod_id in (480,513,514,540);

alter Table T_PRODUKT modify p_bit_daten NUMBER (10);
alter Table T_PRODUKT modify p_bit_voip NUMBER (10);

update T_PRODUKT t set t.P_BIT_DATEN = 1 , t.P_BIT_VOIP = 5
where t.prod_id = 540;

update T_PRODUKT t set t.P_BIT_DATEN = 0 , t.P_BIT_VOIP = 5
where t.prod_id in (480,513,514);
