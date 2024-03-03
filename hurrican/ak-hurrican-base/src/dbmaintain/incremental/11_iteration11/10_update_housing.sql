update t_produkt set prod_id=370 where prod_id=360;
update t_produkt set AKTIONS_ID=2 where prod_id=370;

insert into t_produkt_mapping (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE, VERSION)
  values (370, 370, 370, 'housing', 0);

