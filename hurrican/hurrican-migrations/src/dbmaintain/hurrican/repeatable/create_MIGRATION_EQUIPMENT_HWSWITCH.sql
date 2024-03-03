create or replace force VIEW MIGRATION_EQUIPMENT_HWSWITCH as
-- Equipments mit Rack-Zuordnung, die nicht vom Typ DLU oder LTG sind, mit bzw. ohne zugeordneter Rangierung/Auftrag
-- Die Equipments muessen einen Switch haben, oder der zugeordnete Auftrag eine VOIP Leistung
select
    eq.EQ_ID as EQUIPMENT_ID,
    rang.RANGIER_ID,
    at.AUFTRAG_ID
from T_EQUIPMENT eq
    join T_HW_BAUGRUPPE bg on eq.HW_BAUGRUPPEN_ID = bg.ID
    join T_HW_RACK rack on bg.RACK_ID = rack.ID
    left join T_RANGIERUNG rang on eq.EQ_ID = rang.EQ_IN_ID and rang.GUELTIG_BIS > sysdate
    left join T_ENDSTELLE es on es.RANGIER_ID = rang.RANGIER_ID
    left join T_AUFTRAG_TECHNIK at on at.AT_2_ES_ID = es.ES_GRUPPE and at.GUELTIG_BIS > sysdate and at.HW_SWITCH is null
    left join T_AUFTRAG_2_TECH_LS atls on at.AUFTRAG_ID = atls.AUFTRAG_ID and (atls.AKTIV_BIS is null or atls.AKTIV_BIS > sysdate)
    left join T_TECH_LEISTUNG tl on atls.TECH_LS_ID = tl.ID
where eq.GUELTIG_BIS > sysdate and ((eq.switch is not null and rack.RACK_TYP not in ('DLU', 'LTG'))
                                     or (tl.TYP = 'VOIP'))
group by eq.EQ_ID, rang.RANGIER_ID, at.AUFTRAG_ID

union

-- Auftraege mit VOIP Leistung ohne zugeordnetem Port (e.g. SIP-Trunk)
select
    null as EQUIPMENT_ID,
    null as RANGIER_ID,
    at.AUFTRAG_ID
from T_AUFTRAG_TECHNIK at
    join T_AUFTRAG_DATEN ad on at.AUFTRAG_ID = ad.AUFTRAG_ID and ad.GUELTIG_BIS > sysdate
    join T_ENDSTELLE es on at.AT_2_ES_ID = es.ES_GRUPPE and es.ES_TYP = 'B'
    join T_AUFTRAG_2_TECH_LS atls on at.AUFTRAG_ID = atls.AUFTRAG_ID and (atls.AKTIV_BIS is null or atls.AKTIV_BIS > sysdate)
    join T_TECH_LEISTUNG tl on atls.TECH_LS_ID = tl.ID
 where at.GUELTIG_BIS > sysdate and at.HW_SWITCH is null and ad.STATUS_ID < 9800 and ad.STATUS_ID not in (1150, 3400) and tl.TYP = 'VOIP' and es.RANGIER_ID is null
;

grant select on MIGRATION_EQUIPMENT_HWSWITCH to R_HURRICAN_USER;
