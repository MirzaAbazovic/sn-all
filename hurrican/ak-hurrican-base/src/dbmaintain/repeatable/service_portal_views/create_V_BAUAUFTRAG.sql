-- Ermittlung aller Bauauftraege
create or replace view V_HURRICAN_BAUAUFTRAG as
select v.ID as VERLAUF_ID, v.AUFTRAG_ID,  
v.VERLAUF_STATUS_ID, vs.VERLAUF_STATUS, a.KUNDE__NO, ad.PROD_ID, ad.PRODAK_ORDER__NO,  
ad.BEARBEITER as AUFTRAG_BEARBEITER, t.TDN, vpn.VPN_NR, p.ANSCHLUSSART as PRODUKT,  
pg.AM_RESPONSIBILITY, v.NOT_POSSIBLE, AT.NIEDERLASSUNG_ID, NL.TEXT as NIEDERLASSUNG,
at.PROJECT_RESPONSIBLE as PROJECTRESPONSIBLE
from T_VERLAUF v 
inner join T_VERLAUF_STATUS vs on vs.ID=v.VERLAUF_STATUS_ID 
inner join T_AUFTRAG a on a.ID=v.AUFTRAG_ID 
inner join T_AUFTRAG_DATEN ad on ad.AUFTRAG_ID=a.ID 
inner join T_PRODUKT p on ad.PROD_ID=p.PROD_ID 
inner join T_PRODUKTGRUPPE pg on p.PRODUKTGRUPPE_ID=pg.ID 
left join T_AUFTRAG_TECHNIK at on at.AUFTRAG_ID=a.ID 
left join T_NIEDERLASSUNG nl on AT.NIEDERLASSUNG_ID=nl.ID
left join T_TDN t on at.TDN_ID=t.ID 
left join T_VPN vpn on at.VPN_ID=vpn.VPN_ID  
where v.PROJEKTIERUNG='0' 
and ad.GUELTIG_VON<=sysdate and ad.GUELTIG_BIS>sysdate 
and (at.GUELTIG_VON is null or at.GUELTIG_VON<=sysdate) 
and (at.GUELTIG_BIS is null or at.GUELTIG_BIS>sysdate);

GRANT SELECT ON V_HURRICAN_BAUAUFTRAG TO HURRICAN_TECH_MUC;

-- Ermittlung der Detail-Datensaetze zu den Bauauftraegen
create or replace view V_HURRICAN_BA_DETAILS as
select va.*, ABT.TEXT as ABTEILUNG from t_verlauf_abteilung va,
 V_HURRICAN_BAUAUFTRAG B,
 T_ABTEILUNG abt 
 where B.VERLAUF_ID=VA.VERLAUF_ID
 and VA.ABTEILUNG_ID=ABT.ID;

GRANT SELECT ON V_HURRICAN_BA_DETAILS TO HURRICAN_TECH_MUC; 
