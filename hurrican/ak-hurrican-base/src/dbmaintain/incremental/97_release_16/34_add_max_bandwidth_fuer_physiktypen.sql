-- FttB MUC
update t_physiktyp
set max_bandwidth_upstream = 100000
where id = 700;

-- FTTB_VDSL2
update t_physiktyp
set max_bandwidth_upstream = 100000
where id = 800;

-- FTTH
update t_physiktyp
set max_bandwidth_downstream = 1000000,
    max_bandwidth_upstream   = 1000000
where id = 803;

-- FTTH_ETH
update t_physiktyp
set max_bandwidth_downstream = 1000000,
    max_bandwidth_upstream   = 1000000
where id = 808;
