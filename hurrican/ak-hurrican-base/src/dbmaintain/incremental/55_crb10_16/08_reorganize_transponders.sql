-- Migration der Transponder-Eintraege aus T_AUFTRAG_HOUSING_KEY nach T_TRANSPONDER
alter table t_transponder modify transponder_group_id number(19,0) null;
alter table t_transponder add housing_key_tmp number(10) null;

insert into t_transponder t  (t.id, T.TRANSPONDER_ID, T.CUST_FIRSTNAME, T.CUST_LASTNAME, t.housing_key_tmp)
  select s_t_transponder_0.nextVal, key.transponder_id, KEY.CUSTOMER_FIRSTNAME, KEY.CUSTOMER_LASTNAME, key.id from t_auftrag_housing_key key;
  
update t_auftrag_housing_key key set key.transponder_id =
  (select t.id from t_transponder t where T.HOUSING_KEY_TMP=key.id);

alter table t_auftrag_housing_key drop column customer_firstname;
alter table t_auftrag_housing_key drop column customer_lastname;

-- foreign key von auftraghousing-key auf transponder!
ALTER TABLE T_AUFTRAG_HOUSING_KEY ADD CONSTRAINT FK_HOUSINGKEY2TRANSP
  FOREIGN KEY (TRANSPONDER_ID) REFERENCES T_TRANSPONDER (ID);
  
alter table t_transponder drop column housing_key_tmp;


-- uniqueConstraint auf housing-key fuer auftrag/transponderidtranspondergruppe
alter table T_AUFTRAG_HOUSING_KEY ADD CONSTRAINT UQ_HOUSINGKEYTRANSPONDER UNIQUE (AUFTRAG_HOUSING_ID, TRANSPONDER_ID, TRANSPONDER_GROUP_ID);
