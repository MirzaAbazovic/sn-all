--HUR-27815 Leistung Buisness-CPE (ID 325) nicht als default bei Prod 542 hinterlegen
update t_prod_2_tech_leistung set is_default=0 where prod_id=542 and tech_ls_id = 325;
