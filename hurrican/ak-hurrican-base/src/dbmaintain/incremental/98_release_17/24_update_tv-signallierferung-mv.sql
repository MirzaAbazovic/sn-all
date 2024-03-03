-- Leistungen werden in Hurrican nicht benötigt

delete from T_PROD_2_TECH_LEISTUNG where PROD_ID = '522';
delete from T_TECH_LEISTUNG where id >= '151' and id <= '156';

update T_PRODUKT set
 braucht_buendel = '0',
 elverlauf = '1',
 verteilung_durch = 4,
 ba_ruecklaeufer = '1',
 verlauf_chain_id = 28,
 verlauf_cancel_chain_id = 15,
 ba_change_realdate = '1'
where prod_id = 522;

-- BA Anlaesse konfigurieren
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (522, 1);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (522, 3);
