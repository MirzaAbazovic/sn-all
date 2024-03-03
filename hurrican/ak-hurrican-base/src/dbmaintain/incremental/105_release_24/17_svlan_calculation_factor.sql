alter table t_fttx_ekp_frame_contract add svlan_faktor number(10, 0) DEFAULT 50 not null;
update t_fttx_ekp_frame_contract set svlan_faktor = 20 where frame_contract_id = '1UND1-001';
