-- Produktmapping
-- �ndern der ext_prod__no f�r SIP Trunk von '580' auf '590'
update t_produkt_mapping set ext_prod__no = '590' where mapping_group = '580' and prod_id = '580';