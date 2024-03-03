create or replace view V_AUFTRAG_2_VLAN as
select distinct AD.PRODAK_ORDER__NO, VLAN.CVLAN_TYP, 
case
  when VLAN.CVLAN_TYP = 'MC' then 'Multicast'
  else 'UNICAST'
end as Typ, 
VLAN.CVLAN, VLAN.SVLAN_EKP, VLAN.SVLAN_MDU, VLAN.SVLAN_OLT, RACKOLT.GERAETEBEZ, OLT.VLAN_AKTIV_AB 
from T_AUFTRAG_DATEN ad
join T_AUFTRAG_TECHNIK at on (AD.AUFTRAG_ID = AT.AUFTRAG_ID)
join T_ENDSTELLE es on (AT.AT_2_ES_ID = ES.ES_GRUPPE)
join T_RANGIERUNG rang on (RANG.ES_ID = ES.ID)
join T_EQ_VLAN vlan on (VLAN.EQUIPMENT_ID = RANG.EQ_IN_ID)
join T_EQUIPMENT eq on (EQ.EQ_ID = RANG.EQ_IN_ID)
join T_HW_BAUGRUPPE baugr on (BAUGR.ID = EQ.HW_BAUGRUPPEN_ID)
join T_HW_RACK rack on (RACK.ID = BAUGR.RACK_ID)
left join T_HW_RACK_MDU mdu on (MDU.RACK_ID = rack.ID)
join T_HW_RACK_OLT olt on ((MDU.OLT_RACK_ID = OLT.RACK_ID) or (RACK.ID = OLT.RACK_ID))
join T_HW_RACK rackOlt on (OLT.RACK_ID = RACKOLT.ID)
where AT.GUELTIG_BIS = to_date('01.01.2200', 'dd.mm.yyyy')
and vlan.GUELTIG_VON <= OLT.VLAN_AKTIV_AB
and vlan.GUELTIG_BIS > OLT.VLAN_AKTIV_AB
;