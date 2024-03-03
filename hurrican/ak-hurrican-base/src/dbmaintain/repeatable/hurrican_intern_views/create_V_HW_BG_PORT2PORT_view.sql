CREATE OR REPLACE FORCE VIEW V_HW_BG_PORT2PORT
(
   PORT2PORT_ID,
   HW_BG_CHANGE_ID,
   EQ_ID_OLD,
   HW_EQN_OLD,
   EQ_ID_NEW,
   HW_EQN_NEW,
   PORT_STATE_OLD,
   AUFTRAG_ID,
   TAIFUN_ORDER_NO,
   AUFTRAG_STATUS,
   PRODUKT_NAME,
   VPN_NR,
   LAST_SUCCESSFUL_CPS_TX,
   EQ_OLD_MANUAL_CONFIGURATION,
   EQ_NEW_MANUAL_CONFIGURATION
)
AS
   SELECT P2P.ID AS PORT2PORT_ID,
          P2P.HW_BG_CHANGE_ID,
          EQ_OLD.EQ_ID AS EQ_ID_OLD,
          EQ_OLD.HW_EQN AS HW_EQN_OLD,
          EQ_NEW.EQ_ID AS EQ_ID_NEW,
          EQ_NEW.HW_EQN AS HW_EQN_NEW,
          P2P.EQ_STATE_ORIG_OLD AS PORT_STATE_OLD,
          AD.AUFTRAG_ID,
          AD.PRODAK_ORDER__NO AS TAIFUN_ORDER_NO,
          AST.STATUS_TEXT AS AUFTRAG_STATUS,
          PROD.ANSCHLUSSART AS PRODUKT_NAME,
          VPN.VPN_NR,
          CPS.ID AS LAST_SUCCESSFUL_CPS_TX,
          EQ_OLD.MANUAL_CONFIGURATION AS EQ_OLD_MANUAL_CONFIGURATION,
          EQ_NEW.MANUAL_CONFIGURATION AS EQ_NEW_MANUAL_CONFIGURATION
     FROM T_HW_BG_CHANGE_PORT2PORT p2p
          LEFT JOIN T_HW_BG_CHANGE hwbgc ON p2p.HW_BG_CHANGE_ID = hwbgc.ID
          LEFT JOIN T_EQUIPMENT eq_new ON P2P.EQ_ID_NEW = EQ_NEW.EQ_ID
          LEFT JOIN T_EQUIPMENT eq_old ON P2P.EQ_ID_OLD = EQ_OLD.EQ_ID
          LEFT JOIN T_RANGIERUNG rang
             ON (((eq_old.EQ_ID = RANG.EQ_IN_ID
                    OR EQ_OLD.EQ_ID = RANG.EQ_OUT_ID) and hwbgc.change_state_ref_id < 22153)
                 or ((EQ_NEW.EQ_ID=RANG.EQ_IN_ID
                        or EQ_NEW.EQ_ID=RANG.EQ_OUT_ID) and hwbgc.change_state_ref_id >= 22153)
             )
          LEFT JOIN T_ENDSTELLE es ON es.RANGIER_ID = rang.rangier_id
          LEFT JOIN T_ENDSTELLE esadd
             ON esadd.RANGIER_ID_ADDITIONAL = rang.RANGIER_ID
          LEFT JOIN T_AUFTRAG_TECHNIK atech
             ON ES.ES_GRUPPE = ATECH.AT_2_ES_ID
          LEFT JOIN T_AUFTRAG_DATEN ad ON ATECH.AUFTRAG_ID = AD.AUFTRAG_ID
          LEFT JOIN T_AUFTRAG_STATUS ast ON ad.STATUS_ID = AST.ID
          LEFT JOIN T_PRODUKT prod ON AD.PROD_ID = PROD.PROD_ID
          LEFT JOIN T_VPN vpn ON ATECH.VPN_ID = VPN.VPN_ID
          LEFT JOIN T_CPS_TX cps
             ON (    AD.PRODAK_ORDER__NO = CPS.TAIFUN_ORDER__NO
                 AND cps.TX_STATE = 14260)
    WHERE     (rang.gueltig_bis IS NULL OR rang.gueltig_bis > SYSDATE)
          AND (ATECH.GUELTIG_BIS IS NULL OR ATECH.GUELTIG_BIS > SYSDATE)
          AND (AD.GUELTIG_BIS IS NULL OR AD.GUELTIG_BIS > SYSDATE)
          AND (   AD.STATUS_ID IS NULL
               OR (    AD.STATUS_ID NOT IN (1150, 3400)
                   AND (    AD.STATUS_ID <= 9800
                        AND (   ad.KUENDIGUNG IS NULL
                             OR AD.KUENDIGUNG > hwbgc.PLANNED_DATE))))
          AND (   CPS.ID IS NULL
               OR cps.ID =
                     (SELECT MAX (cpstmp.ID) AS MAX_CPS_ID
                        FROM T_CPS_TX cpstmp
                       WHERE     CPSTMP.TAIFUN_ORDER__NO =
                                    AD.PRODAK_ORDER__NO
                             AND CPS.TX_STATE = 14260));


GRANT SELECT ON V_HW_BG_PORT2PORT TO R_HURRICAN_READ_ONLY;
GRANT SELECT ON V_HW_BG_PORT2PORT TO R_HURRICAN_USER;

