-- Account-Art und Acc.-Vorsatz herausnehmen
update t_produkt set account_vors = null, li_nr = null where prod_id = 580;

-- Änderungsgruppen setzen
insert into T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) values (580, 1);
insert into T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) values (580, 4);
