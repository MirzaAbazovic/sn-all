CREATE OR REPLACE FORCE VIEW ACCOUNT_TO_ORDER AS
   SELECT                                                                   --
         ad.AUFTRAG_ID AS HURRICAN_AUFTRAG_ID,
          ad.prodak_order__no AS TAIFUN_ORDER__NO,
          ad.status_id AS AUFTRAG_STATUS_ID,
          ads.STATUS_TEXT AS AUFTRAG_STATUS,
          prod.ANSCHLUSSART AS TECH_PRODUKT,
          acc.ACCOUNT AS ACCOUNT,
          vpn.VPN_NR AS VPN_ID,
          tdn.TDN AS VERBINDUNGSBEZ
   FROM
    T_AUFTRAG_DATEN ad
    INNER JOIN T_AUFTRAG_STATUS ads ON ad.status_id=ads.id
    INNER JOIN T_AUFTRAG_TECHNIK atech ON ad.auftrag_id = atech.auftrag_id
    INNER JOIN T_INT_ACCOUNT acc ON atech.INT_ACCOUNT_ID = acc.ID
    LEFT JOIN T_VPN vpn ON atech.VPN_ID = vpn.VPN_ID
    LEFT JOIN T_TDN tdn ON atech.TDN_ID = tdn.id
    LEFT JOIN T_PRODUKT prod ON ad.PROD_ID = prod.PROD_ID
   WHERE
         ad.gueltig_bis > SYSDATE AND
         atech.gueltig_bis > SYSDATE AND
         ad.status_id <= 9800;

grant select on ACCOUNT_TO_ORDER to R_HURRICAN_TOOLS;
grant select on ACCOUNT_TO_ORDER to R_HURRICAN_TOOLS;
