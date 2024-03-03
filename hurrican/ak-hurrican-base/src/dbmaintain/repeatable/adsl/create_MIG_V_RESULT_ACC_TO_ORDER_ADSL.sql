CREATE OR REPLACE FORCE VIEW MIG_V_RESULT_ACC_TO_ORDER_ADSL AS
   SELECT                                                                   --
         ad.AUFTRAG_ID AS HURRICAN_AUFTRAG_ID,
          ad.prodak_order__no AS TAIFUN_ORDER__NO,
          ad.status_id AS AUFTRAG_STATUS_ID,
          ads.STATUS_TEXT AS AUFTRAG_STATUS,
          prod.ANSCHLUSSART AS TECH_PRODUKT,
          acc.ACCOUNT AS ACCOUNT,
          vpn.VPN_NR AS VPN_ID,
          tdn.TDN AS VERBINDUNGSBEZ,
          EQDSLAM.HW_EQN,
          CC.BRAS_INNER,
          CC.BRAS_OUTER,
          DP.DOWNSTREAM,
          DP.UPSTREAM,
          DP.FASTPATH,
          DP.UETV,
          DP.NAME
   FROM                                     T_AUFTRAG_DATEN ad
                                          INNER JOIN
                                            T_AUFTRAG_STATUS ads ON ad.status_id=ads.id
                                      INNER JOIN
                                         T_AUFTRAG_TECHNIK atech
                                      ON ad.auftrag_id = atech.auftrag_id
                                   INNER JOIN
                                      T_INT_ACCOUNT acc
                                   ON atech.INT_ACCOUNT_ID = acc.ID
                                LEFT JOIN
                                   T_VPN vpn
                                ON atech.VPN_ID = vpn.VPN_ID
                             LEFT JOIN
                                T_TDN tdn
                             ON atech.TDN_ID = tdn.id
                          LEFT JOIN
                             T_ENDSTELLE es
                          ON atech.AT_2_ES_ID = es.ES_GRUPPE
                       LEFT JOIN
                          T_RANGIERUNG r
                       ON es.RANGIER_ID = r.RANGIER_ID
                    LEFT JOIN
                       T_EQUIPMENT eqdslam
                    ON r.EQ_IN_ID = eqdslam.EQ_ID
                 LEFT JOIN
                    T_EQ_CROSS_CONNECTION cc
                 ON eqdslam.EQ_ID = cc.EQUIPMENT_ID
              LEFT JOIN
                 T_AUFTRAG_2_DSLAMPROFILE a2d
              ON ad.auftrag_id = A2D.AUFTRAG_ID
           LEFT JOIN
              T_DSLAM_PROFILE dp
           ON A2D.DSLAM_PROFILE_ID = DP.ID
        LEFT JOIN
           T_PRODUKT prod
        ON ad.PROD_ID = prod.PROD_ID
   WHERE
        acc.li_nr IN (1, 3, 4) AND
         ad.gueltig_bis > SYSDATE AND
         atech.gueltig_bis > SYSDATE AND
         ad.PROD_ID NOT IN (50, 310, 311) AND
         (ad.status_id < 9800 AND ad.status_id NOT IN (1150, 3400)) AND
         (A2D.GUELTIG_BIS > SYSDATE OR A2D.GUELTIG_BIS IS NULL) AND
         (A2D.GUELTIG_VON <= SYSDATE OR A2D.GUELTIG_VON IS NULL) AND
         (ES.ES_TYP = 'B' OR es.ES_TYP IS NULL) AND
         (R.GUELTIG_BIS > SYSDATE OR R.GUELTIG_BIS IS NULL) AND
         ( (cc.CC_TYPE_REF_ID IN (20000, 20005, 20010) AND
            cc.VALID_TO > SYSDATE) OR
          cc.CC_TYPE_REF_ID IS NULL);

create or replace view MIG_V_RESULT_ACC_ALWAYSON as
  select A2TLS.TECH_LS_ID as ALWAYS_ON
  from t_auftrag_2_tech_ls a2tls
  where A2TLS.TECH_LS_ID=3 and A2TLS.AKTIV_BIS>SYSDATE
;

grant select on MIG_V_RESULT_ACC_TO_ORDER_ADSL to R_HURRICAN_TOOLS;
grant select on MIG_V_RESULT_ACC_ALWAYSON to R_HURRICAN_TOOLS;
