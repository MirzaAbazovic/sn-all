-- Lfd. OLT_NR fuer VLAN-Berechnung ergaenzen

alter table T_HW_RACK_OLT ADD (
    OLT_NR NUMBER(10)
);

alter table T_HW_RACK_OLT add constraint UK_HW_RACK_OLT_NR unique (OLT_NR);

update T_HW_RACK_OLT set olt_nr = 1 where rack_id = ( select id from t_hw_rack where geraetebez = 'OLT-400028' );
update T_HW_RACK_OLT set olt_nr = 2 where rack_id = ( select id from t_hw_rack where geraetebez = 'OLT-400060' );
update T_HW_RACK_OLT set olt_nr = 3 where rack_id = ( select id from t_hw_rack where geraetebez = 'OLT-400035' );
update T_HW_RACK_OLT set olt_nr = 4 where rack_id = ( select id from t_hw_rack where geraetebez = 'OLT-400042' );
update T_HW_RACK_OLT set olt_nr = 5 where rack_id = ( select id from t_hw_rack where geraetebez = 'OLT-400027' );
update T_HW_RACK_OLT set olt_nr = 6 where rack_id = ( select id from t_hw_rack where geraetebez = 'OLT-400061' );
update T_HW_RACK_OLT set olt_nr = 7 where rack_id = ( select id from t_hw_rack where geraetebez = 'OLT-400033' );
update T_HW_RACK_OLT set olt_nr = 8 where rack_id = ( select id from t_hw_rack where geraetebez = 'OLT-400034' );
update T_HW_RACK_OLT set olt_nr = 9 where rack_id = ( select id from t_hw_rack where geraetebez = 'OLT-400059' );
update T_HW_RACK_OLT set olt_nr = 10 where rack_id = ( select id from t_hw_rack where geraetebez = 'OLT-400003' );
update T_HW_RACK_OLT set olt_nr = 11 where rack_id = ( select id from t_hw_rack where geraetebez = 'OLT-400005' );
update T_HW_RACK_OLT set olt_nr = 12 where rack_id = ( select id from t_hw_rack where geraetebez = 'OLT-400002' );
update T_HW_RACK_OLT set olt_nr = 13 where rack_id = ( select id from t_hw_rack where geraetebez = 'OLT-400001' );
update T_HW_RACK_OLT set olt_nr = 14 where rack_id = ( select id from t_hw_rack where geraetebez = 'OLT-400008' );
update T_HW_RACK_OLT set olt_nr = 15 where rack_id = ( select id from t_hw_rack where geraetebez = 'OLT-400020' );
update T_HW_RACK_OLT set olt_nr = 16 where rack_id = ( select id from t_hw_rack where geraetebez = 'OLT-400016' );
update T_HW_RACK_OLT set olt_nr = 17 where rack_id = ( select id from t_hw_rack where geraetebez = 'OLT-400015' );
update T_HW_RACK_OLT set olt_nr = 18 where rack_id = ( select id from t_hw_rack where geraetebez = 'OLT-400014' );
