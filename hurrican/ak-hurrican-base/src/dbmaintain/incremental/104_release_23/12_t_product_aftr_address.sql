ALTER TABLE T_PRODUKT ADD AFTR_ADDRESS varchar2(2000);

update T_PRODUKT t set t.AFTR_ADDRESS = 'aftr.prod.m-online.net'
where t.prod_id in (512, 513, 514, 515);
