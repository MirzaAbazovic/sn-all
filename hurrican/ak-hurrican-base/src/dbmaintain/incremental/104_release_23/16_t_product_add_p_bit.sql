update T_PRODUKT t set t.AFTR_ADDRESS = 'aftr.prod.m-online.net'
where t.prod_id = 480;

-- neue Attribute für P_BIT_DATEN und P_BIT_VOIP für LoginZugangsdaten ANF-694
ALTER TABLE T_PRODUKT ADD P_BIT_DATEN NUMBER(19);
ALTER TABLE T_PRODUKT ADD P_BIT_VOIP NUMBER(19);

update T_PRODUKT t set t.P_BIT_DATEN = 1 , t.P_BIT_VOIP = 5
where t.prod_id = 540;

update T_PRODUKT t set t.P_BIT_DATEN = 0 , t.P_BIT_VOIP = 5
where t.prod_id in (480,513,514);
