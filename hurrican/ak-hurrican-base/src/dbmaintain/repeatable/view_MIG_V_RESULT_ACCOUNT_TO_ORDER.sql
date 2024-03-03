-- View fuer das SDSL-Migrationsergebnis
--  Result: Taifun Auftragsnummer / Account-Name / VPN Rahmenvertrag

create or replace view MIG_V_RESULT_ACCOUNT_TO_ORDER as
  select 
    ad.prodak_order__no as TAIFUN_ORDER__NO,
    acc.ACCOUNT as ACCOUNT,
    vpn.VPN_NR as VPN_ID
  from 
    MIG_LOG mig
    left join T_AUFTRAG_DATEN ad 
        on ((SUBSTR(mig.DEST_VALUE, 0, 6)=ad.auftrag_id) OR (SUBSTR(mig.DEST_VALUE, 8)=ad.auftrag_id))
    left join T_AUFTRAG_TECHNIK atech on ad.auftrag_id=atech.auftrag_id
    inner join T_INT_ACCOUNT acc on atech.INT_ACCOUNT_ID=acc.ID
    left join T_VPN vpn on atech.VPN_ID=vpn.VPN_ID
  where
    mig.DEST_TABLE='T_AUFTRAG'
    and ad.gueltig_bis>SYSDATE 
    and atech.gueltig_bis>SYSDATE
    and ad.status_id<9800
    ;



