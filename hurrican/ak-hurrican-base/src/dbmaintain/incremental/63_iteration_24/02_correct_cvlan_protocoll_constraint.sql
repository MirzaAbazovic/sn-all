alter table T_CVLAN drop constraint CONSTR_CVLAN_PROTOCOLL;
alter table T_CVLAN add constraint CONSTR_CVLAN_PROTOCOLL check (PROTOCOLL in ('PPPoE', 'IPoE'));